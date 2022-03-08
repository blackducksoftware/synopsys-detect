package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.junit.jupiter.api.Assertions;

import com.github.dockerjava.api.model.Bind;

public class DockerTestDirectories {
    private final File testDirectory;
    private final File testResultDirectory;
    private final List<Bind> bindings = new ArrayList<>();

    private File bdioOutputDirectory;
    private File detectOutputDirectory;

    public DockerTestDirectories(String testId) throws IOException {
        File dockerTestDirectory;
        if (!SystemUtils.IS_OS_WINDOWS) {
            Set<PosixFilePermission> allWriteablePermissions = PosixFilePermissions.fromString("rwxrwxrwx");
            FileAttribute<?> allWriteabeAttribute = PosixFilePermissions.asFileAttribute(allWriteablePermissions);
            dockerTestDirectory = Files.createTempDirectory("docker", allWriteabeAttribute).toFile();
        } else {
            dockerTestDirectory = Files.createTempDirectory("docker").toFile();
        }
        testDirectory = new File(dockerTestDirectory, testId);
        testResultDirectory = new File(testDirectory, "result");
        Assertions.assertTrue(testResultDirectory.mkdirs());
    }

    public void withBinding(File localDirectory, String imageDirectory) throws IOException {
        bindings.add(Bind.parse(localDirectory.getCanonicalPath() + ":" + imageDirectory));
    }

    public File createResultDirectory(String name) {
        File resultDirectory = new File(testResultDirectory, name);

        if (resultDirectory.exists()) {
            try {
                FileUtils.deleteDirectory(resultDirectory);
            } catch (IOException e) {
                Assertions.assertNull(e, "Could not delete " + name + " directory.");
            }
        }

        Assertions.assertTrue(resultDirectory.mkdir(), String.format("Failed to create container directory at: %s", resultDirectory.getAbsolutePath()));
        return resultDirectory;
    }

    public String bdioBinding() throws IOException {
        String imagePath = "/opt/results/bdio";
        bdioOutputDirectory = createResultDirectory("bdio");
        withBinding(bdioOutputDirectory, imagePath);
        return imagePath;
    }

    public String detectOutputPathBinding() throws IOException {
        String imagePath = "/opt/results/output";
        detectOutputDirectory = createResultDirectory("output");
        withBinding(detectOutputDirectory, imagePath);
        return imagePath;
    }

    public void cleanup() throws IOException {
        //File rootTestDir = testDirectory.getParentFile();
        //FileUtils.deleteDirectory(rootTestDir);
    }

    public Bind[] getBindings() {
        return bindings.toArray(new Bind[0]);
    }

    public File getResultOutputDirectory() {
        return detectOutputDirectory;
    }

    public File getResultBdioDirectory() {
        return bdioOutputDirectory;
    }
}
