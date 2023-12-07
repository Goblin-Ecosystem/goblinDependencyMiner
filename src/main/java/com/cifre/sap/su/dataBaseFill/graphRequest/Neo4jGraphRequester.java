package com.cifre.sap.su.dataBaseFill.graphRequest;

import com.cifre.sap.su.utils.YmlConfReader;
import org.neo4j.driver.*;

public class Neo4jGraphRequester {
    private final Driver driver;

    public Neo4jGraphRequester() {
        YmlConfReader conf = YmlConfReader.getInstance();
        driver =  GraphDatabase.driver(conf.getNeo4jUri(), AuthTokens.basic(conf.getNeo4jUser(), conf.getNeo4jPassword()));
    }

    public long getMaxTimestamp(){
        try (Session session = driver.session()) {
            String query = "MATCH (n:Release) RETURN max(n.timestamp) AS maxTimestamp";
            return session.run(query)
                    .single()
                    .get("maxTimestamp").asLong();
        }
    }

    public void close() {
        driver.close();
    }
}
