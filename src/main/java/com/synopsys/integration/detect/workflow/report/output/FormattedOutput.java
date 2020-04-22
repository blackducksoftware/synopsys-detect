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
package com.synopsys.integration.detect.workflow.report.output;

import java.util.ArrayList;
import java.util.List;

public class FormattedOutput {
    private String formatVersion = "";
    private String detectVersion = "";
    private String projectName = "";
    private String projectVersion = "";
    private List<FormattedDetectorOutput> detectors = new ArrayList<>();

    private List<FormattedStatusOutput> status = new ArrayList<>();
    private List<FormattedIssueOutput> issues = new ArrayList<>();
    private List<FormattedResultOutput> results = new ArrayList<>();
    private List<FormattedCodeLocationOutput> codeLocations = new ArrayList<>();

    public String getFormatVersion() {
        return formatVersion;
    }

    public void setFormatVersion(final String formatVersion) {
        this.formatVersion = formatVersion;
    }

    public String getDetectVersion() {
        return detectVersion;
    }

    public void setDetectVersion(final String detectVersion) {
        this.detectVersion = detectVersion;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersion() {
        return projectVersion;
    }

    public void setProjectVersion(final String projectVersion) {
        this.projectVersion = projectVersion;
    }

    public List<FormattedDetectorOutput> getDetectors() {
        return detectors;
    }

    public void setDetectors(final List<FormattedDetectorOutput> detectors) {
        this.detectors = detectors;
    }

    public List<FormattedStatusOutput> getStatus() {
        return status;
    }

    public void setStatus(final List<FormattedStatusOutput> status) {
        this.status = status;
    }

    public List<FormattedIssueOutput> getIssues() {
        return issues;
    }

    public void setIssues(final List<FormattedIssueOutput> issues) {
        this.issues = issues;
    }

    public List<FormattedResultOutput> getResults() {
        return results;
    }

    public void setResults(final List<FormattedResultOutput> results) {
        this.results = results;
    }

    public List<FormattedCodeLocationOutput> getCodeLocations() {
        return codeLocations;
    }

    public void setCodeLocations(final List<FormattedCodeLocationOutput> codeLocations) {
        this.codeLocations = codeLocations;
    }
}

