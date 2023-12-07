package com.cifre.sap.su.dataBaseFill.maven;

import com.cifre.sap.su.model.Release;
import com.cifre.sap.su.utils.FileUtils;
import com.cifre.sap.su.utils.LoggerWriter;
import jakarta.persistence.EntityExistsException;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MavenLuceneIndexReader {
    final String mavenIndexFolderPath = "central-lucene-index";

    /**
     * Get all releases present on the Maven Central Lucene Index from a given timestamp.
     * @param fromTimestamp If you want to generate from scratch, set 0. If you want de update, set the max timestamp of you current dataset.
     * @return Set of Releases.
     */
    public Set<Release> getAllIndexRelease(long fromTimestamp){
        LoggerWriter.info("Generate all maven release from index");
        Long startTime = System.currentTimeMillis();
        Set<Release> releases = new HashSet<>();
        try {
            Directory dir = FSDirectory.open(new File(mavenIndexFolderPath));
            IndexReader r = IndexReader.open(dir);
            int num = r.numDocs();
            int errors = 0;
            for ( int i = 0; i < num; i++)
            {
                Document d = r.document(i);
                if(d.toString().contains("|NA")){
                    String id = d.get("u");
                    String timestampString = d.get("i");

                    if(id != null && timestampString != null && id.split("\\|").length == 4 && !id.contains(" ")){
                        String[] idSplit = id.split("\\|");
                        long timestamp = Long.parseLong(timestampString.split("\\|")[1]);
                        if(timestamp > fromTimestamp){
                            String gav = idSplit[0].replace("\"","").replace("'", "") + ":" + idSplit[1].replace("\"","").replace("'", "") + ":" + idSplit[2].replace("\"","").replace("'", "");
                            try{
                                releases.add(new Release(gav, timestamp));
                            } catch(EntityExistsException e){
                                LoggerWriter.warn(e.toString());
                            }
                        }
                    }
                    else{
                        FileUtils.writeExistFile(d+"\n", "output"+File.separator+"maven_index_recovery_errors.txt");
                        errors ++;
                    }
                }
            }
            r.close();
            Long endTime = System.currentTimeMillis();
            recoveryMetrics(errors, startTime, endTime);
        } catch (IOException e) {
            LoggerWriter.error("Failed to create vertices via maven index:\n"+e);
        }
        LoggerWriter.info("Releases found: "+releases.size());
        return releases;
    }

    private void recoveryMetrics(int errors, Long startTime, Long endTime){
        // export result
        long milliseconds = startTime - endTime;
        int seconds = (int) milliseconds / 1000;
        // calculate hours minutes and seconds
        String duration = seconds / 3600 + "h" + (seconds % 3600) / 60 + "min" + (seconds % 3600) % 60 + "s";
        String content = "Duration time: " + duration + "\n" +
                "Number of errors: " + errors + "\n";
        FileUtils.createFile(content, "output"+File.separator+"maven_index_recovery_metrics.txt");
    }
}
