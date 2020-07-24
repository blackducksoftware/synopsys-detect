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
    public String codeLocationId;

    @SerializedName("scannerVersion")
    public String scannerVersion;

    @SerializedName("signatureVersion")
    public String signatureVersion;

    @SerializedName("id")
    public String id;

    @SerializedName("scanType")
    public String scanType;

    @SerializedName("name")
    public String codeLocationName;

    @SerializedName("hostName")
    public String hostName;

    @SerializedName("baseDir")
    public String baseDir;

    @SerializedName("ownerEntityKeyToken")
    public String ownerEntityKeyToken;

    @SerializedName("createdOn")
    public String createdOn;

    @SerializedName("timeToScan")
    public Integer timeToScan;

    @SerializedName("createdByUserId")
    public String createdByUserId;

    @SerializedName("status")
    public String status;

    @SerializedName("statusMessage")
    public String statusMessage;

    @SerializedName("matchCount")
    public Integer matchCount;

    @SerializedName("numDirs")
    public Integer numberOfDirectories;

    @SerializedName("numNonDirFiles")
    public Integer numberOfNonDirectoryFiles;

    @SerializedName("scanSourceType")
    public String scanSourceType;

    @SerializedName("scanSourceId")
    public String scanSourceId;

    @SerializedName("scanTime")
    public Integer scanTime;

    @SerializedName("timeLastModified")
    public Integer timeLastModified;

    @SerializedName("timeToPersistMs")
    public Integer timeToPersistMs;

    @SerializedName("arguments")
    public JsonObject arguments2;
}
