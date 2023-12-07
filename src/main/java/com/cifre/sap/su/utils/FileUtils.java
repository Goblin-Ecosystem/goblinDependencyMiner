package com.cifre.sap.su.utils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class FileUtils {

    public static void createDirectory(String directoryPath){
        try {
            Path path = Paths.get(directoryPath);
            Files.createDirectories(path);
        } catch (IOException e) {
            LoggerWriter.warn("Failed to create directory: " + e.getMessage());
        }
    }

    public static void deleteDirectoryIfExist(String directoryPath){
        Path path = Paths.get(directoryPath);
        if (Files.exists(path)) {
            try {
                Files.walk(Paths.get(directoryPath))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
            } catch (IOException e) {
                LoggerWriter.warn("Unable to delete folder: " + directoryPath);
            }
        }
    }

    public static void writeExistFile(String content, String filePath) {
        Path path = Paths.get(filePath);
        createDirectory(path.getParent().toString());
        try {
            Files.write(
                    path,
                    content.getBytes(),
                    CREATE, APPEND);
        } catch (IOException e) {
            LoggerWriter.warn("Fail to write on file "+filePath+" file :\n"+e.getMessage());
        }
    }

    public static void createFile(String content, String filePath){
        Path path = Paths.get(filePath);
        createDirectory(path.getParent().toString());
        try {
            FileWriter myWriter = new FileWriter(filePath);
            myWriter.write(content);
            myWriter.close();
        } catch (IOException e) {
            LoggerWriter.warn("Fail to create "+filePath+" file :\n"+e.getMessage());
        }
    }

    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        file.delete();
    }

    public static void downloadFile(String fileUrl, String destination) throws IOException {
        InputStream in = new URL(fileUrl).openStream();
        Files.copy(in, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
    }
}
