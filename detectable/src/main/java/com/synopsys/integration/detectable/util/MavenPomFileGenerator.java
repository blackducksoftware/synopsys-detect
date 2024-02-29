package com.synopsys.integration.detectable.util;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.executable.ExecutableOutput;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MavenPomFileGenerator {

    private final String BASE_URL = "https://repo1.maven.org/maven2";
    private final DetectableExecutableRunner executableRunner;
    private final ExecutableTarget mavenExe;

    public MavenPomFileGenerator(DetectableExecutableRunner executableRunner, ExecutableTarget mavenExe) {
        this.executableRunner = executableRunner;
        this.mavenExe = mavenExe;
    }

    public Path createNewDirectory() throws IOException, SecurityException, IOError {
        return Files.createTempDirectory("dependency_pom_files").toAbsolutePath();
    }

    public void generatePomFile(Path directoryPath, Dependency dependency, String extension) throws ExecutableFailedException {
        List<String> commandArguments = generateMavenCliPomArguments(dependency, extension, directoryPath);
        ExecutableOutput mvnJarFileOutput = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directoryPath.toFile(), mavenExe, commandArguments));
    }

    public void generateJarFile(Path directoryPath, Dependency dependency) throws ExecutableFailedException {
        List<String> commandArguments = generateMavenCliPomArguments(dependency, "", directoryPath);
        ExecutableOutput mvnPomFileOutput = executableRunner.executeSuccessfully(ExecutableUtils.createFromTarget(directoryPath.toFile(), mavenExe, commandArguments));
    }

    private List<String> generateMavenCliPomArguments(Dependency dependency, String extension, Path directoryPath) {
        List<String> arguments = new ArrayList<>();

        String group = dependency.getExternalId().getGroup();
        String artifact = dependency.getName();
        String version = dependency.getVersion();

        List<String> gav = new ArrayList<>(Arrays.asList(group,artifact,version));

        arguments.add("dependency:copy");
        arguments.add("-Dartifact="+String.join(":",gav)+":"+extension);
        arguments.add("-DoutputDirectory="+directoryPath.toString());

        return arguments;
    }

    public void extractJar(String jarFilePath, File destDir) {
        if(destDir.exists()) {
            try (JarFile jarFile = new JarFile(jarFilePath)) {
                Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements()){
                    JarEntry entry = entries.nextElement();
                    File entryFile = new File(destDir,entry.getName());
                    if(entry.isDirectory()) {
                        entryFile.mkdir();
                    } else {
                        try (InputStream inputStream = jarFile.getInputStream(entry);
                             FileOutputStream outputStream = new FileOutputStream(entryFile)) {
                            while (inputStream.available() > 0) {
                                outputStream.write(inputStream.read());
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException();
            }
        }
    }

//    public String addGAVtoURL(Dependency dependency, String extension){
//
//        String group = dependency.getExternalId().getGroup();
//        String artifact = dependency.getName();
//        String version = dependency.getVersion();
//
//        group = group.replace(".","/");
//        List<String> url = new ArrayList<>(Arrays.asList(BASE_URL,group,artifact,version,artifact+"-"+version+"."+extension));
//        return String.join("/",url);
//    }

}


