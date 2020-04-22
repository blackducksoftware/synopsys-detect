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

public class FormattedDetectorOutput {
    private String folder = "";
    private String detectorType = "";
    private String detectorName = "";
    private String descriptiveName = "";

    private boolean searchable = true;
    private boolean applicable = true;
    private boolean extractable = true;
    private boolean discoverable = true;
    private boolean extracted = true;

    private String searchableReason = "";
    private String applicableReason = "";
    private String extractableReason = "";
    private String discoveryReason = "";
    private String extractedReason = "";

    private List<String> relevantFiles = new ArrayList<>();

    private String projectName = "";
    private String projectVersion = "";
    private int codeLocationCount = 0;

    public String getFolder() {
        return folder;
    }

    public void setFolder(final String folder) {
        this.folder = folder;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(final String detectorType) {
        this.detectorType = detectorType;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(final String detectorName) {
        this.detectorName = detectorName;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public void setDescriptiveName(final String descriptiveName) {
        this.descriptiveName = descriptiveName;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(final boolean searchable) {
        this.searchable = searchable;
    }

    public boolean isApplicable() {
        return applicable;
    }

    public void setApplicable(final boolean applicable) {
        this.applicable = applicable;
    }

    public boolean isExtractable() {
        return extractable;
    }

    public void setExtractable(final boolean extractable) {
        this.extractable = extractable;
    }

    public boolean isDiscoverable() {
        return discoverable;
    }

    public void setDiscoverable(final boolean discoverable) {
        this.discoverable = discoverable;
    }

    public boolean isExtracted() {
        return extracted;
    }

    public void setExtracted(final boolean extracted) {
        this.extracted = extracted;
    }

    public String getSearchableReason() {
        return searchableReason;
    }

    public void setSearchableReason(final String searchableReason) {
        this.searchableReason = searchableReason;
    }

    public String getApplicableReason() {
        return applicableReason;
    }

    public void setApplicableReason(final String applicableReason) {
        this.applicableReason = applicableReason;
    }

    public String getExtractableReason() {
        return extractableReason;
    }

    public void setExtractableReason(final String extractableReason) {
        this.extractableReason = extractableReason;
    }

    public String getDiscoveryReason() {
        return discoveryReason;
    }

    public void setDiscoveryReason(final String discoveryReason) {
        this.discoveryReason = discoveryReason;
    }

    public String getExtractedReason() {
        return extractedReason;
    }

    public void setExtractedReason(final String extractedReason) {
        this.extractedReason = extractedReason;
    }

    public List<String> getRelevantFiles() {
        return relevantFiles;
    }

    public void setRelevantFiles(final List<String> relevantFiles) {
        this.relevantFiles = relevantFiles;
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

    public int getCodeLocationCount() {
        return codeLocationCount;
    }

    public void setCodeLocationCount(final int codeLocationCount) {
        this.codeLocationCount = codeLocationCount;
    }
}

