/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.detector.go;

import com.google.gson.annotations.SerializedName;

public class SolveMeta {
    @SerializedName("inputs-digest")
    private String inputsDigest;

    @SerializedName("analyzer-name")
    private String analyzerName;

    @SerializedName("analyzer-version")
    private Integer analyzerVersion;

    @SerializedName("solver-name")
    private String solverName;

    @SerializedName("solver-version")
    private Integer solverVersion;

    public String getInputsDigest() {
        return inputsDigest;
    }

    public void setInputsDigest(final String inputsDigest) {
        this.inputsDigest = inputsDigest;
    }

    public String getAnalyzerName() {
        return analyzerName;
    }

    public void setAnalyzerName(final String analyzerName) {
        this.analyzerName = analyzerName;
    }

    public Integer getAnalyzerVersion() {
        return analyzerVersion;
    }

    public void setAnalyzerVersion(final Integer analyzerVersion) {
        this.analyzerVersion = analyzerVersion;
    }

    public String getSolverName() {
        return solverName;
    }

    public void setSolverName(final String solverName) {
        this.solverName = solverName;
    }

    public Integer getSolverVersion() {
        return solverVersion;
    }

    public void setSolverVersion(final Integer solverVersion) {
        this.solverVersion = solverVersion;
    }

}
