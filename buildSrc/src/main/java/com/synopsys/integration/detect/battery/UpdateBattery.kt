/**
 * buildSrc
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.battery

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import java.io.File

// Occasionally changes to BDIO or other integral systems require mass changes to the battery. This facilitates that work.
open class UpdateBatteryTask : DefaultTask() {

    @TaskAction
    fun updateBattery() {
        val batteryBuild = File("build/battery/")
        //battery.listFiles()
        val batteryResources = File("src/test/resources/battery/")

        val batteryReports = File("build/reports/tests/testBattery/classes/")
        batteryReports.listFiles().forEach { report ->
            report.useLines { lines ->
                lines.forEach { line: String ->
                    if (line.contains("***BDIO BATTERY TEST|")) {
                        val chunk = line.substringAfter("***BDIO BATTERY TEST|").substringBefore("***")
                        val pieces = chunk.split("|");
                        val testName = pieces[0];
                        val resourcePrefix = pieces[1];
                        val bdioName = pieces[2];

                        val testFolder = File(batteryResources, resourcePrefix)
                        val bdioFolder = File(testFolder, "bdio")
                        val bdioFile = File(bdioFolder, bdioName)
                        //println(bdioFile.toString() + ":" + bdioFile.exists())

                        val actualTestFolder = File(batteryBuild, testName)
                        val actualBdioFolder = File(actualTestFolder, "bdio")
                        val actualBdioFile = File(actualBdioFolder, bdioName)

                        //println(actualBdioFile.toString() + ":" + actualBdioFile.exists())

                        val json = JsonParser.parseString(actualBdioFile.readText()).asJsonArray;
                        val headerElement = json.get(0).asJsonObject;
                        headerElement.remove("@id");
                        headerElement.remove("creationInfo");
                        val gson = GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX").setPrettyPrinting().create()
                        bdioFile.writeText(gson.toJson(json));

                        println("COPIED FROM: $actualBdioFile")
                        println("COPIED TO  : $bdioFile")

                    }
                }
            }
        }
    }
}