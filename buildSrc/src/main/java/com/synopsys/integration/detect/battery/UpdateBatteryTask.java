/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.regex.Pattern;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.synopsys.integration.exception.IntegrationException;

// Occasionally changes to BDIO or other integral systems require mass changes to the battery. This facilitates that work.
public class UpdateBatteryTask extends DefaultTask {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").setPrettyPrinting().create();

    @TaskAction
    public void updateBattery() throws IOException, IntegrationException {
        File batteryBuild = new File("build/battery/");
        File batteryResources = new File("src/test/resources/battery/");
        File batteryReports = new File("build/reports/tests/testBattery/classes/");

        if (!batteryReports.exists() || !batteryReports.isDirectory()) {
            throw new IntegrationException("Battery reports directory doesn't exist. ("+ batteryReports.getCanonicalPath() +")");
        }

        for (File report : Objects.requireNonNull(batteryReports.listFiles())) {
            for (String line : Files.readAllLines(report.toPath())) {
                if (line.contains("***BDIO BATTERY TEST|")) {
                    String chunk = extractChunk(line);
                    String[] pieces = chunk.split("\\|");
                    String testName = pieces[0];
                    String resourcePrefix = pieces[1];
                    String bdioName = pieces[2];

                    File testFolder = new File(batteryResources, resourcePrefix);
                    File bdioFolder = new File(testFolder, "bdio");
                    File bdioFile = new File(bdioFolder, bdioName);

                    File actualTestFolder = new File(batteryBuild, testName);
                    File actualBdioFolder = new File(actualTestFolder, "bdio");
                    File actualBdioFile = new File(actualBdioFolder, bdioName);

                    JsonArray json = JsonParser.parseString(new String(Files.readAllBytes(actualBdioFile.toPath()))).getAsJsonArray();
                    JsonObject headerElement = json.get(0).getAsJsonObject();
                    headerElement.remove("@id");
                    headerElement.remove("creationInfo");
                    try (FileWriter fileWriter = new FileWriter(bdioFile)) {
                        fileWriter.write(gson.toJson(json));
                    }

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
