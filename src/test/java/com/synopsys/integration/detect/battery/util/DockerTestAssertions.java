package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Objects;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;
import com.synopsys.integration.detect.battery.docker.util.DockerDetectResult;
import com.synopsys.integration.detect.battery.docker.util.DockerTestDirectories;
import com.synopsys.integration.detect.workflow.report.output.FormattedDetectorOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;

public class DockerTestAssertions {
    private DockerDetectResult dockerDetectResult;
    private final File outputDirectory;
    private final File bdioDirectory;
    private FormattedOutput statusJson = null;

    public DockerTestAssertions(final DockerTestDirectories testDirectories, final DockerDetectResult dockerDetectResult) {
        this.dockerDetectResult = dockerDetectResult;
        this.outputDirectory = testDirectories.getResultOutputDirectory();
        this.bdioDirectory = testDirectories.getResultBdioDirectory();
    }

    public void successfulDetectorType(String detectorType) {
        successfulDetectorTypeLogged(detectorType);
        successfulDetectorTypeStatusJson(detectorType);
    }

    public void successfulDetectorTypeLogged(String detectorType) {
        Assertions.assertTrue(dockerDetectResult.getDetectLogs().contains(detectorType + ": SUCCESS"));
    }

    public void successfulDetectorTypeStatusJson(String detectorType) {
        FormattedOutput statusJson = locateStatusJson();
        Optional<FormattedDetectorOutput> detector = statusJson.detectors.stream().filter(it -> it.detectorType.equals(detectorType))
                                                         .findFirst();

        Assertions.assertTrue(detector.isPresent(), "Could not find required detector in status json detector list.");

        Assertions.assertEquals("SUCCESS", detector.get().status);
    }

    private FormattedOutput locateStatusJson() {
        if (statusJson != null)
            return statusJson;

        File runs = new File(outputDirectory, "runs");
        File[] runDirectories = runs.listFiles();
        Assertions.assertNotNull(runDirectories, "Could not find any run directories, looked in: " + runs.toString());
        Assertions.assertEquals(1, runDirectories.length, "There should be exactly one run directory (from this latest run).");

        File run = runDirectories[0];
        File status = new File(run, "status");
        Assertions.assertTrue(status.exists(), "Could not find status directory in the run directory!");
        File statusJsonFile = new File(status, "status.json");
        Assertions.assertTrue(statusJsonFile.exists(), "Could not find status json in the status directory!");

        try {
            statusJson = new Gson().fromJson(new FileReader(statusJsonFile), FormattedOutput.class);
        } catch (FileNotFoundException e) {
            Assertions.fail("Unable to parse status json with gson.", e);
        }
        return statusJson;
    }

    public void atLeastOneBdioFile() {
        Assertions.assertNotNull(bdioDirectory, "Expected at least one bdio file!");
        Assertions.assertNotNull(bdioDirectory.listFiles(), "Expected at least one bdio file!");
        Assertions.assertTrue(Objects.requireNonNull(bdioDirectory.listFiles()).length > 0, "Expected at least one bdio file!");
    }
}
