package com.cifre.sap.su.utils;

import org.yaml.snakeyaml.Yaml;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class YmlConfReader {
    private final Map<String, Object> confMap;
    private static YmlConfReader INSTANCE;

    private YmlConfReader() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("configuration.yml");
        confMap = yaml.load(inputStream);
    }

    public static synchronized YmlConfReader getInstance()
    {
        if (INSTANCE == null)
        {   INSTANCE = new YmlConfReader();
        }
        return INSTANCE;
    }

    public List<String> getDataBaseToExport(){
        return (List<String>) confMap.get("dataBaseExport");
    }

    public boolean isUpdate(){
        return (boolean) confMap.get("update");
    }

    public int getNbThread(){
        int nbThread = (int) confMap.get("thread");
        return Math.max(nbThread, 1);
    }

    public String getNeo4jUri(){
        return (String) confMap.get("neo4jUri");
    }

    public String getNeo4jUser(){
        return (String) confMap.get("neo4jUser");
    }

    public String getNeo4jPassword(){
        return (String) confMap.get("neo4jPassword");
    }

}
