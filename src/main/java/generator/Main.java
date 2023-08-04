package generator;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;



public class Main {
    public static void main(String[] args) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String initializrUrl = "https://start.spring.io/starter.zip";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("type", "maven-project");
        map.add("language", "java");
        map.add("bootVersion", "3.1.0");
        map.add("baseDir", "myproject");
        map.add("groupId", "com.example");
        map.add("artifactId", "demo");
        map.add("name", "demo");
        map.add("description", "Demo project");
        map.add("packageName", "com.example.demo");
        map.add("packaging", "jar");
        map.add("javaVersion", "11");
        map.add("dependencies", "web,data-jpa");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        ResponseEntity<byte[]> response = restTemplate.postForEntity(initializrUrl, request, byte[].class);

        String filePath = "/Users/fsk/Desktop/coding/myproject.zip";

        if (response.getStatusCode() == HttpStatus.OK) {
            // Yanıtı bir dosyaya yazma
            Files.write(Path.of(filePath), response.getBody());
            System.out.println("Proje başarıyla oluşturuldu!");
        } else {
            System.out.println("Proje oluşturma başarısız: " + response.getStatusCode());
        }

        byte[] buffer = new byte[1024];
        String destination = "/Users/fsk/Desktop/coding/";

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

        String rootPath = "/Users/fsk/Desktop/coding/myproject/src/main/java/com/example/demo";
        String packageName = "entities";

        // Paket adını dizin yapısına dönüştür
        String packagePath = packageName.replace('.', '/');

        // Tam yol oluştur
        Path path = Paths.get(rootPath, packagePath);

        try {
            // Dizinleri oluştur
            Files.createDirectories(path);
            System.out.println("Paket oluşturuldu: " + path);
        } catch (FileAlreadyExistsException e) {
            System.out.println("Paket zaten var: " + path);
        } catch (IOException e) {
            System.out.println("Paket oluşturulamadı: " + e.getMessage());
        }

        //Path pathh = Paths.get("/Users/fsk/Desktop/coding/myproject/src/main/resources/application.properties");

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