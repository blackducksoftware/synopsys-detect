/**
 * synopsys-detect
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
package com.synopsys.integration.detect.tool.impactanalysis.service;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ImpactAnalysisSuccessResult {

    @SerializedName("codeLocationId")
    public final String codeLocationId;

    @SerializedName("scannerVersion")
    public final String scannerVersion;

    @SerializedName("signatureVersion")
    public final String signatureVersion;

    @SerializedName("id")
    public final String id;

    @SerializedName("scanType")
    public final String scanType;

    @SerializedName("name")
    public final String codeLocationName;

    @SerializedName("hostName")
    public final String hostName;

    @SerializedName("baseDir")
    public final String baseDir;

    @SerializedName("ownerEntityKeyToken")
    public final String ownerEntityKeyToken;

    @SerializedName("createdOn")
    public final String createdOn;

    @SerializedName("timeToScan")
    public final Integer timeToScan;

    @SerializedName("createdByUserId")
    public final String createdByUserId;

    @SerializedName("status")
    public final String status;

    @SerializedName("statusMessage")
    public final String statusMessage;

    @SerializedName("matchCount")
    public final Integer matchCount;

    @SerializedName("numDirs")
    public final Integer numberOfDirectories;

    @SerializedName("numNonDirFiles")
    public final Integer numberOfNonDirectoryFiles;

    @SerializedName("scanSourceType")
    public final String scanSourceType;

    @SerializedName("scanSourceId")
    public final String scanSourceId;

    @SerializedName("scanTime")
    public final Integer scanTime;

    @SerializedName("timeLastModified")
    public final Integer timeLastModified;

    @SerializedName("timeToPersistMs")
    public final Integer timeToPersistMs;

    @SerializedName("arguments")
    public final JsonObject arguments;

    public ImpactAnalysisSuccessResult(String codeLocationId, String scannerVersion, String signatureVersion, String id, String scanType, String codeLocationName, String hostName, String baseDir, String ownerEntityKeyToken,
        String createdOn, Integer timeToScan, String createdByUserId, String status, String statusMessage, Integer matchCount, Integer numberOfDirectories, Integer numberOfNonDirectoryFiles, String scanSourceType, String scanSourceId,
        Integer scanTime, Integer timeLastModified, Integer timeToPersistMs, JsonObject arguments) {
        this.codeLocationId = codeLocationId;
        this.scannerVersion = scannerVersion;
        this.signatureVersion = signatureVersion;
        this.id = id;
        this.scanType = scanType;
        this.codeLocationName = codeLocationName;
        this.hostName = hostName;
        this.baseDir = baseDir;
        this.ownerEntityKeyToken = ownerEntityKeyToken;
        this.createdOn = createdOn;
        this.timeToScan = timeToScan;
        this.createdByUserId = createdByUserId;
        this.status = status;
        this.statusMessage = statusMessage;
        this.matchCount = matchCount;
        this.numberOfDirectories = numberOfDirectories;
        this.numberOfNonDirectoryFiles = numberOfNonDirectoryFiles;
        this.scanSourceType = scanSourceType;
        this.scanSourceId = scanSourceId;
        this.scanTime = scanTime;
        this.timeLastModified = timeLastModified;
        this.timeToPersistMs = timeToPersistMs;
        this.arguments = arguments;
    }
}
