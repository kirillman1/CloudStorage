package kirillgontov.cloudstorage.server.service;

import kirillgontov.cloudstorage.server.util.Configuration;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сохраняет файлы, отдает файлы по запросу
 */

public class FileService {
    public static void createUserFolder(String folderName) throws IOException {
        Files.createDirectory(Paths.get(Configuration.PATH + folderName));
    }

    /*public static byte[] getFileList(String folderName) throws IOException {
        List<Path> listPaths = Files.list(Paths.get(Configuration.PATH + folderName)).collect(Collectors.toList());
//TODO
        byte[] listPathsBytes = Files.readAllBytes(listPaths);
    }*/

    public static void uploadFile(String folderName, String fileName, byte[] fileBytes) throws IOException {
        Files.write(Paths.get(Configuration.PATH + folderName + "/" + fileName), fileBytes, StandardOpenOption.CREATE_NEW);
    }

    public static void deleteFile(String folderName, String fileName) throws IOException {
        Files.deleteIfExists(Paths.get(Configuration.PATH + folderName + "/" + fileName));
    }




}
