package com.cifre.sap.su.dataBaseFill.graphGenerator;

import com.cifre.sap.su.dataBaseFill.graphInstruction.*;
import com.cifre.sap.su.model.Link;
import com.cifre.sap.su.model.Release;
import com.cifre.sap.su.utils.YmlConfReader;
import org.neo4j.driver.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Neo4JGenerator extends DatabaseGenerator {
    private final Driver driver;
    private final int nbThread;

    public Neo4JGenerator() {
        YmlConfReader conf = YmlConfReader.getInstance();
        nbThread = conf.getNbThread();
        driver =  GraphDatabase.driver(conf.getNeo4jUri(), AuthTokens.basic(conf.getNeo4jUser(), conf.getNeo4jPassword()));
    }

    @Override
    public void accept(GraphInstruction graphInstruction) {
        if(graphInstruction instanceof InitInstruction){
            try (Session session = driver.session()) {
                session.run("CREATE CONSTRAINT artifactConstraint IF NOT EXISTS FOR (n:Artifact) REQUIRE n.id IS UNIQUE").consume();
                session.run("CREATE CONSTRAINT releaseConstraint IF NOT EXISTS FOR (n:Release) REQUIRE n.id IS UNIQUE").consume();
            }
        }
        else if(graphInstruction instanceof CreateVertexInstruction){
            Iterator<Release> releaseIterator = ((Set<Release>) graphInstruction.getObject()).iterator();
            ExecutorService executorService = Executors.newFixedThreadPool(nbThread);
            for (int i = 0; i < nbThread; i++) {
                executorService.submit(() -> {
                while (true) {
                    Release release;
                    synchronized (releaseIterator) {
                        if (!releaseIterator.hasNext()) {
                            break;
                        }
                        release = releaseIterator.next();
                    }
                    try (Session session = driver.session()) {
                        String[] gavSplit = release.getGav().split(":");
                        // Prepare parameters
                        Map<String, Object> parameters = new HashMap<>();
                        parameters.put("artifactId", gavSplit[0] + ":" + gavSplit[1]);
                        parameters.put("releaseGav", release.getGav());
                        parameters.put("releaseVersion", gavSplit[2]);
                        parameters.put("releaseTimestamp", release.getTimestamp());
                        // Merge artifact
                        try {
                            String queryMergeArtifact = "MERGE (b:Artifact {id: $artifactId, found: true})";
                            session.run(queryMergeArtifact, parameters).consume();
                        } catch (Exception ignored) {}
                        // Create release
                        try {
                            String queryCreateRelease = "CREATE (c:Release {id: $releaseGav, version: $releaseVersion, timestamp: $releaseTimestamp})";
                            session.run(queryCreateRelease, parameters).consume();
                        } catch (Exception ignored) {}
                        // Create relationship
                        try {
                            String queryCreateRelation = "MATCH (b:Artifact {id: $artifactId}), (c:Release {id: $releaseGav}) CREATE (b)-[:relationship_AR]->(c)";
                            session.run(queryCreateRelation, parameters).consume();
                        } catch (Exception ignored) {}
                    }
                }
                });
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Wait until all threads are finish
            }
        }
        else if(graphInstruction instanceof CreateEdgeInstruction) {
            Iterator<Link> linkIterator = ((List<Link>) graphInstruction.getObject()).iterator();
            ExecutorService executorService = Executors.newFixedThreadPool(nbThread);
            for (int i = 0; i < nbThread; i++) {
                executorService.submit(() -> {
                    while (true) {
                        Link link;
                        synchronized (linkIterator) {
                            if (!linkIterator.hasNext()) {
                                break;
                            }
                            link = linkIterator.next();
                        }
                        try (Session session = driver.session()) {
                            // Prepare parameters
                            Map<String, Object> parameters = new HashMap<>();
                            parameters.put("sourceId", link.getSource());
                            parameters.put("targetArtifactId", link.getTargetArtifact());
                            parameters.put("targetVersion", link.getTargetVersion());
                            parameters.put("scope", link.getScope());
                            // Create not found artifact if not exist
                            try {
                            String queryMergeArtifact = "MERGE (b:Artifact {id: $targetArtifactId}) ON CREATE SET b.id = $targetArtifactId, b.found = false";
                            session.run(queryMergeArtifact, parameters).consume();
                            } catch (Exception ignored) {}
                            // Create dependency
                            try {
                                String queryCreateDependency = "MATCH (r:Release {id: $sourceId}), (b:Artifact {id: $targetArtifactId}) CREATE (r)-[:dependency {targetVersion: $targetVersion, scope: $scope}]->(b)";
                                session.run(queryCreateDependency, parameters).consume();
                            } catch (Exception ignored) {}

                        }
                    }
                });
            }
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                // Wait until all threads are finish
            }
        }
    }
}
