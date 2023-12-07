package com.cifre.sap.su.dataBaseFill.maven;

import com.cifre.sap.su.dataBaseFill.DatabaseFiller;
import com.cifre.sap.su.dataBaseFill.graphGenerator.DatabaseGenerator;
import com.cifre.sap.su.dataBaseFill.graphGenerator.Neo4JGenerator;
import com.cifre.sap.su.dataBaseFill.graphGenerator.SqlGenerator;
import com.cifre.sap.su.dataBaseFill.graphInstruction.*;
import com.cifre.sap.su.dataBaseFill.graphRequest.Neo4jGraphRequester;
import com.cifre.sap.su.model.Link;
import com.cifre.sap.su.model.Release;
import com.cifre.sap.su.utils.FileUtils;
import com.cifre.sap.su.utils.LoggerWriter;
import com.cifre.sap.su.utils.YmlConfReader;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.Dependency;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MavenFiller implements DatabaseFiller {
    private final int BATCH = 100000;

    public void generateDataset(){
        LoggerWriter.info("Generate Maven Central graph from scratch");
        if(setupMavenCentralIndex()) {
            MavenLuceneIndexReader indexReader = new MavenLuceneIndexReader();
            Set<Release> releases = indexReader.getAllIndexRelease(0);
            fillReleases(releases);
            fillDependencies(releases);
        }
    }

    public void updateDataset(){
        // Get latest release timestamp on the graph
        // TODO: Here, we can not update if we only have an SQL version
        LoggerWriter.info("Update Maven Central graph");
        if(setupMavenCentralIndex()) {
            Neo4jGraphRequester graphRequester = new Neo4jGraphRequester();
            long maxReleaseTimestamp = graphRequester.getMaxTimestamp();
            graphRequester.close();
            // Get new releases on the Maven Luncene Index
            MavenLuceneIndexReader indexReader = new MavenLuceneIndexReader();
            Set<Release> releases = indexReader.getAllIndexRelease(maxReleaseTimestamp);
            // Generates nodes and relations
            fillReleases(releases);
            fillDependencies(releases);
        }
    }

    private void fillReleases (Set<Release> releases){
        LoggerWriter.info("Generate releases on database");
        List<DatabaseGenerator> generators = getGeneratorList();
        generators.forEach(generator -> generator.accept(new InitInstruction()));
        generators.forEach(generator -> generator.accept(new CreateVertexInstruction(releases)));
    }

    private void fillDependencies (Set<Release> releases){
        LoggerWriter.info("Generate dependencies on database");
        MavenDependenciesCollector mavenDependenciesCollector = new MavenDependenciesCollector();
        List<DatabaseGenerator> generators = getGeneratorList();
        int totalNbRelease = releases.size();
        int nbIteration = 0;
        int batchCpt = 0;
        while (releases.size() != 0) {
            LoggerWriter.info(nbIteration*BATCH + "/" + totalNbRelease);
            nbIteration++;
            Set<Release> releasesBatch = new HashSet<>();
            for (Iterator<Release> it = releases.iterator(); it.hasNext(); ) {
                releasesBatch.add(it.next());
                batchCpt++;
                it.remove();
                if (batchCpt % BATCH == 0) {
                    break;
                }
            }
            // Get dependencies
            try {
                List<Link> linkList = releasesBatch.parallelStream()
                        .map(release -> recoverAndAddDependencies(release, mavenDependenciesCollector))
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
                // Add dependencies to database
                generators.forEach(generator -> generator.accept(new CreateEdgeInstruction(linkList)));
                //Clear workspace
                FileUtils.deleteDirectoryIfExist("workspace");
                FileUtils.createDirectory("workspace");
            } catch (Exception e){
                LoggerWriter.error(e.toString());
                e.printStackTrace();
            }
        }
    }

    private List<Link> recoverAndAddDependencies(Release release, MavenDependenciesCollector mavenDependenciesCollector){
        List<Link> linkList = new ArrayList<>();
        List<Dependency> dependencies = mavenDependenciesCollector.getArtifactDirectDependencies(release.getId());
        for(Dependency dependency : dependencies){
            Artifact targetArtifact = dependency.getArtifact();
            String artifact = targetArtifact.getGroupId() + ":" + targetArtifact.getArtifactId();
            linkList.add(new Link(release.getId(), artifact.replace("\"","").replace("'", ""), targetArtifact.getVersion().replace("\"","").replace("'", ""), dependency.getScope()));
        }
        return linkList;
    }

    private List<DatabaseGenerator> getGeneratorList(){
        List<DatabaseGenerator> generators = new ArrayList<>();
        for(String database : YmlConfReader.getInstance().getDataBaseToExport()){
            switch (database.toUpperCase()) {
                case "POSTGRES" -> generators.add(new SqlGenerator());
                case "NEO4J" -> generators.add(new Neo4JGenerator());
                default -> {
                }
            }
        }
        return generators;
    }

    private boolean setupMavenCentralIndex(){
        try {
            FileUtils.deleteDirectoryIfExist("central-lucene-index");
            LoggerWriter.info("Download Maven index");
            FileUtils.downloadFile("https://repo.maven.apache.org/maven2/.index/nexus-maven-repository-index.gz", "nexus-maven-repository-index.gz");
            LoggerWriter.info("Extract Maven index Lucene archive");
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "java",
                    "-jar",
                    "lib/indexer-cli-5.1.1.jar",
                    "--unpack",
                    "nexus-maven-repository-index.gz",
                    "--destination",
                    "central-lucene-index",
                    "--type",
                    "full"
            );
            Process process = processBuilder.start();
            process.waitFor();
            FileUtils.deleteFile("nexus-maven-repository-index.gz");
            return true;
        } catch (IOException | InterruptedException e) {
            LoggerWriter.fatal("Unable to download Maven Central index:\n"+e);
            return false;
        }
    }
}
