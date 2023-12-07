package com.cifre.sap.su;

import com.cifre.sap.su.dataBaseFill.DatabaseFiller;
import com.cifre.sap.su.dataBaseFill.maven.MavenFiller;
import com.cifre.sap.su.utils.FileUtils;
import com.cifre.sap.su.utils.LoggerWriter;
import com.cifre.sap.su.utils.YmlConfReader;

public class App 
{
    public static void main( String[] args )
    {
        // Create directories
        LoggerWriter.info("Start app");
        FileUtils.createDirectory("output");
        DatabaseFiller mavenFiller = new MavenFiller();
        if(YmlConfReader.getInstance().isUpdate()){
            mavenFiller.updateDataset();
        }
        else {
            mavenFiller.generateDataset();
        }
        FileUtils.deleteDirectoryIfExist("central-lucene-index");
        LoggerWriter.info("End app");
    }

}
