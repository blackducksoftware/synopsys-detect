package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.synopsys.integration.exception.IntegrationException;

// Occasionally changes to BDIO or other integral systems require mass changes to the battery. This facilitates that work.
public class UpdateBatteryTask extends DefaultTask {
    String bdioName = "battery.bdio";

    @TaskAction
    public void updateBattery() throws IOException, IntegrationException {

        File projectDir = getProject().getProjectDir();
        System.out.println("Project directory: " + projectDir);

        File batteryBuild = new File(projectDir, "build/battery/");
        File batteryResources = new File(projectDir, "src/test/resources/battery/");
        File batteryReports = new File(projectDir, "build/reports/tests/testBattery/classes/");

        if (!batteryReports.exists() || !batteryReports.isDirectory()) {
            throw new IntegrationException("Battery reports directory doesn't exist. (" + batteryReports.getCanonicalPath() + ")");
        }

        for (File report : Objects.requireNonNull(batteryReports.listFiles())) {
            System.out.println("READING TEST: " + report.toPath());
            for (String line : Files.readAllLines(report.toPath())) {
                if (line.contains("***BDIO BATTERY TEST|")) {
                    String chunk = extractChunk(line);
                    String[] pieces = chunk.split("\\|");
                    String testName = pieces[0];
                    String resourcePrefix = pieces[1];

                    File testFolder = new File(batteryResources, resourcePrefix);
                    File bdioFolder = new File(testFolder, "bdio");

                    File[] children = bdioFolder.listFiles();
                    if (children == null || children.length != 1 || !children[0].getName().equals(bdioName)) {
                        System.out.println("CLEANING: " + bdioFolder);
                        FileUtils.deleteDirectory(bdioFolder);
                        bdioFolder.mkdir();
                    }

                    File bdioFile = new File(bdioFolder, bdioName);
                    File actualTestFolder = new File(batteryBuild, testName);
                    File actualBdioFolder = new File(actualTestFolder, "bdio");
                    File actualBdioFile = new File(actualBdioFolder, bdioName);

                    FileUtils.copyFile(actualBdioFile, bdioFile);

                    System.out.println("COPIED FROM: " + actualBdioFile.getAbsolutePath());
                    System.out.println("COPIED TO  : " + bdioFile.getAbsolutePath());
                }
            }
        }
    }

    private String extractChunk(String line) {
        String chunk = line.split(Pattern.quote("***BDIO BATTERY TEST|"))[1];
        return chunk.split(Pattern.quote("***"))[0];
    }
}
