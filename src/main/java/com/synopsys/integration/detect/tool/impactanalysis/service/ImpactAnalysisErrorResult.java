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

import org.jetbrains.annotations.Nullable;

import com.google.gson.annotations.SerializedName;

public class ImpactAnalysisErrorResult {
    @SerializedName("status")
    public final String status;

    @SerializedName("matchCount")
    public final Integer matchCount;

    @SerializedName("numDirs")
    public final Integer numberOfDirectories;

    @SerializedName("numNonDirFiles")
    public final Integer numberOfNonDirectoryFiles;

    @SerializedName("scanTime")
    public final Integer scanTime;

    @SerializedName("timeLastModified")
    public final Integer timeLastModified;

    @SerializedName("timeToPersistMs")
    public final Integer timeToPersistMs;

    @Nullable
    @SerializedName("errorCode")
    public final String errorMessage;

    public ImpactAnalysisErrorResult(String status, Integer matchCount, Integer numberOfDirectories, Integer numberOfNonDirectoryFiles, Integer scanTime, Integer timeLastModified, Integer timeToPersistMs, @Nullable String errorMessage) {
        this.status = status;
        this.matchCount = matchCount;
        this.numberOfDirectories = numberOfDirectories;
        this.numberOfNonDirectoryFiles = numberOfNonDirectoryFiles;
        this.scanTime = scanTime;
        this.timeLastModified = timeLastModified;
        this.timeToPersistMs = timeToPersistMs;
        this.errorMessage = errorMessage;
    }

}
