/**
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.regex.Pattern;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

// Occasionally changes to BDIO or other integral systems require mass changes to the battery. This facilitates that work.
public class UpdateBatteryTask extends DefaultTask {

    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").setPrettyPrinting().create();

    @TaskAction
    public void updateBattery() throws IOException {
        File batteryBuild = new File("build/battery/");
        File batteryResources = new File("src/test/resources/battery/");
        File batteryReports = new File("build/reports/tests/testBattery/classes/");

        for (File report : batteryReports.listFiles()) {
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
                    FileWriter fileWriter = new FileWriter(bdioFile);
                    fileWriter.write(gson.toJson(json));

                    System.out.println("COPIED FROM: $actualBdioFile");
                    System.out.println("COPIED TO  : $bdioFile");
                }
            }
        }
    }

    private String extractChunk(String line) {
        String chunk = line.split(Pattern.quote("***BDIO BATTERY TEST|"))[1];
        return chunk.split(Pattern.quote("***"))[0];
    }
}
