package generator;

import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class Main {

    private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
    private static Scanner getInfo = new Scanner(System.in);

    public static void main(String[] args) throws IOException {

        RestTemplate restTemplate = new RestTemplate();
        String initializrUrl = Constants.SPRING_IO_URL;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

//        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
//        map.add("type", "maven-project");
//        map.add("language", "java");
//        map.add("bootVersion", "3.1.0");
//        map.add("baseDir", "myproject");
//        map.add("groupId", "com.example");
//        map.add("artifactId", "demo");
//        map.add("name", "demo");
//        map.add("description", "Demo project");
//        map.add("packageName", "com.example.demo");
//        map.add("packaging", "jar");
//        map.add("javaVersion", "11");
//        map.add("dependencies", "web");

        ProjectInformation projectInformation = new ProjectInformation();
        MultiValueMap<String, String> projectInformationMap = projectInformation.projectGenerator();

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(projectInformationMap, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity(initializrUrl, request, byte[].class);

        LOGGER.info("Which location save do you want");
        String filePath = getInfo.nextLine();
        String baseDir = projectInformationMap.get(Constants.BASE_DIR).toString();
        String subs = baseDir.substring(1, baseDir.length() - 1);
        String destinationPath = filePath;
        String filePathWithZip = filePath+"/"+subs+".zip";
        //String filePath = "/Users/fsk/Desktop/coding/myproject.zip";

        saveProjectWithZip(response, filePathWithZip);

        unzip(filePathWithZip, destinationPath);

        //String rootPath = "/Users/fsk/Desktop/coding/myproject/src/main/java/com/example/demo";
        String packageStructure = projectInformationMap.get(Constants.PACKAGE_NAME).toString().replace(".", "/");
        String substringPackageStructure = packageStructure.substring(1, packageStructure.length() - 1);
        String rootPath = filePath+"/"+subs+"/src/main/java/"+substringPackageStructure;
        LOGGER.info("Package Name: ");
        String packageName = getInfo.nextLine();

        // Paket adını dizin yapısına dönüştür
        String packagePath = packageName.replace('.', '/');

        // Tam yol oluştur
        Path path = Paths.get(rootPath, packagePath);

        try {
            // Dizinleri oluştur
            Path directories = Files.createDirectories(path);
            if (Objects.nonNull(directories)) {

            }
            System.out.println("Paket oluşturuldu: " + path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Paket zaten var: " + path);
        } catch (IOException e) {
            System.out.println("Paket oluşturulamadı: " + e.getMessage());
        }

        //Path pathh = Paths.get("/Users/fsk/Desktop/coding/myproject/src/main/resources/application.properties");

    }

    private static void unzip(String filePath, String destinationPath) throws IOException {
        byte[] buffer = new byte[1024];
        String destination = destinationPath;

        File file = new File(destination);
        ZipInputStream zis = new ZipInputStream(new FileInputStream(filePath));

        ZipEntry zipEntry = zis.getNextEntry();

        while (zipEntry != null) {
            File newFile = newFile(file, zipEntry);
            if (zipEntry.isDirectory()) {
                if (!newFile.isDirectory() && !newFile.mkdirs()) {
                    throw new IOException("Failed to create directory " + newFile);
                }
            } else {
                // fix for Windows-created archives
                File parent = newFile.getParentFile();
                if (!parent.isDirectory() && !parent.mkdirs()) {
                    throw new IOException("Failed to create directory " + parent);
                }

                // write file content
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
            }
            zipEntry = zis.getNextEntry();
        }

        zis.closeEntry();
        zis.close();
    }

    private static void saveProjectWithZip(ResponseEntity<byte[]> response, String filePath) throws IOException {
        if (response.getStatusCode() == HttpStatus.OK) {
            // Yanıtı bir dosyaya yazma
            Files.write(Path.of(filePath), response.getBody());
            LOGGER.info("Proje başarıyla oluşturuldu!");
        } else {
            LOGGER.info("Proje oluşturma başarısız: " + response.getStatusCode());
        }
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }
}