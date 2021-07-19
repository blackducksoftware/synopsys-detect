package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;

import com.github.dockerjava.api.model.Bind;

public class DockerTestDirectories {
    private final File testDirectory;
    private final File testResultDirectory;
    private List<Bind> bindings = new ArrayList<>();

    private File bdioOutputDirectory;
    private File detectOutputDirectory;

    public DockerTestDirectories(String testId) throws IOException {
        File dockerTestDirectory = SharedDockerDirectory.getRoot();
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

    public String sharedToolsBinding() throws IOException {
        String imagePath = "/opt/tools";
        withBinding(SharedDockerDirectory.getSharedTools(), imagePath);
        return imagePath;
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
