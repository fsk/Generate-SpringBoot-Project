package generator;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class ProjectInformation {

    Scanner getInfo = new Scanner(System.in);
    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    private static final Logger LOGGER = Logger.getLogger(ProjectInformation.class.getName());
    private final List<String> dependencyList = new ArrayList<>();


    public MultiValueMap<String, String> projectGenerator() {
        provideProjectType();
        provideSoftwareLanguage();
        provideSpringBootVersion();
        provideBaseDir();
        provideGroupId();
        provideArtifactId();
        provideProjectName();
        provideDescription();
        providePackageName();
        providePackaging();
        provideJavaVersion();
        provideDependencies();
        return map;
    }

    private void provideDependencies() {
        LOGGER.info("Provide Dependencies: ");
        String[] dependencies = getInfo.nextLine().split(",");
        this.dependencyList.addAll(Arrays.asList(dependencies));
        String s = dependencyList.toString().replaceAll("\\s", ",");
        s = s.substring(1, s.length() - 1);
        map.add(Constants.DEPENDENCIES, s);
    }

    private void provideJavaVersion() {
        LOGGER.info("Provide Java Version: ");
        String javaVersion = getInfo.nextLine();
        map.add(Constants.JAVA_VERSION, javaVersion);
    }

    private void providePackaging() {
        LOGGER.info("Provide Packaging: ");
        String packaging = getInfo.nextLine();
        map.add(Constants.PACKAGING, packaging);
    }

    private void providePackageName() {
        LOGGER.info("Provide Package Name: ");
        String packageName = getInfo.nextLine();
        map.add(Constants.PACKAGE_NAME, packageName);
    }

    private void provideDescription() {
        LOGGER.info("Provide Description: ");
        String description = getInfo.nextLine();
        map.add(Constants.DESCRIPTION, description);
    }

    private void provideProjectName() {
        LOGGER.info("Provide Project Name: ");
        String projectName = getInfo.nextLine();
        map.add(Constants.PROJECT_NAME, projectName);
    }

    private void provideArtifactId() {
        LOGGER.info("Provide ArtifactId: ");
        String artifactId = getInfo.nextLine();
        map.add(Constants.ARTIFACT_ID, artifactId);
    }

    private void provideGroupId() {
        LOGGER.info("Provide GroupId: ");
        String groupId = getInfo.nextLine();
        map.add(Constants.GROUP_ID, groupId);
    }

    private void provideBaseDir() {
        LOGGER.info("Provide Base Directory: ");
        String baseDir = getInfo.nextLine();
        map.add(Constants.BASE_DIR, baseDir);
    }

    private void provideSpringBootVersion() {
        LOGGER.info("Provide Spring Boot version: ");
        String springBootVersion = getInfo.nextLine();
        map.add(Constants.BOOT_VERSION, springBootVersion);
    }

    private void provideSoftwareLanguage() {
        LOGGER.info("Provide Software Language: ");
        String softwareLanguage = getInfo.nextLine();
        map.add(Constants.LANGUAGE, softwareLanguage);
    }

    private void provideProjectType() {
        LOGGER.info("Provide Project Type: ");
        String projectType = getInfo.nextLine();
        map.add(Constants.TYPE, projectType);
    }
}
