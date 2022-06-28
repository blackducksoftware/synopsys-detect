package com.synopsys.integration.detect.battery.docker.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;

import com.google.gson.Gson;
import com.synopsys.integration.detect.workflow.report.output.FormattedDetectorOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOperationOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedOutput;
import com.synopsys.integration.detect.workflow.report.output.FormattedStatusOutput;
import com.synopsys.integration.util.NameVersion;

public class DockerAssertions {
    private final DockerDetectResult dockerDetectResult;
    private final File outputDirectory;
    private final File bdioDirectory;
    private FormattedOutput statusJson = null;

    public DockerAssertions(DockerTestDirectories testDirectories, DockerDetectResult dockerDetectResult) {
        this.dockerDetectResult = dockerDetectResult;
        this.outputDirectory = testDirectories.getResultOutputDirectory();
        this.bdioDirectory = testDirectories.getResultBdioDirectory();
    }

    public void successfulDetectorType(String detectorType) {
        successfulThingLogged(detectorType);
        successfulDetectorTypeStatusJson(detectorType);
    }

    public void successfulTool(String tool) {
        successfulThingLogged(tool);
        successfulToolStatusJson(tool);
    }

    public void successfulThingLogged(String detectorType) {
        Assertions.assertTrue(dockerDetectResult.getDetectLogs().contains(detectorType + ": SUCCESS"));
    }

    public void successfulDetectorTypeStatusJson(String detectorType) {
        FormattedOutput statusJson = locateStatusJson();
        Optional<FormattedDetectorOutput> detector = statusJson.detectors.stream().filter(it -> it.detectorType.equals(detectorType))
            .findFirst();

        Assertions.assertTrue(detector.isPresent(), "Could not find required detector in status json detector list.");

        Assertions.assertEquals("SUCCESS", detector.get().status);
    }

    public void successfulOperationStatusJson(String operationKey) {
        FormattedOutput statusJson = locateStatusJson();
        Optional<FormattedOperationOutput> detector = statusJson.operations.stream().filter(it -> it.descriptionKey.equals(operationKey))
            .findFirst();

        Assertions.assertTrue(detector.isPresent(), "Could not find required operation '" + operationKey + "' in status json detector list.");

        Assertions.assertEquals("SUCCESS", detector.get().status);
    }

    public void successfulToolStatusJson(String detectorType) {
        FormattedOutput statusJson = locateStatusJson();
        Optional<FormattedStatusOutput> tool = statusJson.status.stream().filter(it -> it.key.equals(detectorType))
            .findFirst();

        Assertions.assertTrue(tool.isPresent(), "Could not find required detector in status json detector list.");

        Assertions.assertEquals("SUCCESS", tool.get().status);
    }

    private FormattedOutput locateStatusJson() {
        if (statusJson != null)
            return statusJson;

        File runs = new File(outputDirectory, "runs");
        File[] runDirectories = runs.listFiles();
        Assertions.assertNotNull(runDirectories, "Could not find any run directories, looked in: " + runs);
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

    public void logContainsPattern(String pattern) {
        Assertions.assertNotNull(pattern);
        Pattern regex = Pattern.compile("(?s).*" + pattern + ".*", Pattern.MULTILINE);
        Assertions.assertTrue(regex.matcher(dockerDetectResult.getDetectLogs()).matches(), "Expected logs to contain '" + regex + "' but they did not.");
    }

    // TODO convenience methods on dockerAssertions for testing whether a detector was ATTEMPTED, SUCCESSFUL, ...

    public void logContains(String thing) {
        Assertions.assertTrue(dockerDetectResult.getDetectLogs().contains(thing), "Expected logs to contain '" + thing + "' but they did not.");
    }

    public void logDoesNotContain(String thing) {
        Assertions.assertFalse(dockerDetectResult.getDetectLogs().contains(thing), "Expected logs to NOT contain '" + thing + "' but they did.");
    }

    public void successfulOperation(String operationName) {
        successfulOperationStatusJson(operationName);
        successfulThingLogged(operationName);
    }

    public void projectVersion(NameVersion nameVersion) {
        projectVersion(nameVersion.getName(), nameVersion.getVersion());
    }

    public void projectVersion(String project, String version) {
        FormattedOutput statusJson = locateStatusJson();
        Assertions.assertEquals(project, statusJson.projectName);
        Assertions.assertEquals(version, statusJson.projectVersion);
        logContains("Project name: " + project); //Should we rely solely on the status json?
        logContains("Project version: " + version);
    }

    public void bdioFiles(int bdioCount) {
        checkBdioDirectory();
        Assertions.assertEquals(bdioCount, Objects.requireNonNull(bdioDirectory.listFiles()).length);
    }

    public void bdioFileCreated(String requiredBdioFilename) {
        checkBdioDirectory();
        Assertions.assertTrue(
            Arrays.asList(bdioDirectory.listFiles()).stream()
                .map(File::getName)
                .filter(requiredBdioFilename::equals)
                .findAny().isPresent(),
            String.format("Expected BDIO file %s, but it was not created", requiredBdioFilename)
        );
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public void resultProducedAtLocation(String location) {
        Assertions.assertTrue(locateStatusJson().results.stream().anyMatch(result -> result.location.equals(location)), "Unable to find result: " + location);
    }

    private void checkBdioDirectory() {
        Assertions.assertNotNull(bdioDirectory, "Bdio directory did not exist!");
        Assertions.assertNotNull(bdioDirectory.listFiles(), "Bdio directory list files was null.");
    }
}
