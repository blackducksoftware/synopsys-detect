/**
 * detect-configuration
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
package com.blackducksoftware.integration.hub.detect.configuration;

import java.io.File;

import org.apache.commons.lang3.BooleanUtils;

public class DetectConfig extends BaseConfig {
    private File sourceDirectory;
    private File outputDirectory;

    public void initialize(final ValueContainer valueContainer, final File sourceDirectory, final File outputDirectory) {
        this.sourceDirectory = sourceDirectory;
        this.outputDirectory = outputDirectory;

        this.detectProjectBomTool = valueContainer.getDetectProjectBomTool();
        this.bomToolSearchDepth = valueContainer.getBomToolSearchDepth();
        this.bomToolContinueSearch = valueContainer.getBomToolContinueSearch();
        this.bomToolSearchExclusion = valueContainer.getBomToolSearchExclusion();
        this.bomToolSearchExclusionDefaults = valueContainer.getBomToolSearchExclusionDefaults();
        this.excludedBomToolTypes = valueContainer.getExcludedBomToolTypes();
        this.includedBomToolTypes = valueContainer.getIncludedBomToolTypes();
        this.failOnConfigWarning = valueContainer.getFailOnConfigWarning();
        this.forceSuccess = valueContainer.getForceSuccess();
        this.suppressConfigurationOutput = valueContainer.getSuppressConfigurationOutput();
        this.suppressResultsOutput = valueContainer.getSuppressResultsOutput();
        this.cleanupDetectFiles = valueContainer.getCleanupDetectFiles();
        this.resolveTildeInPaths = valueContainer.getResolveTildeInPaths();
        this.sourcePath = valueContainer.getSourcePath();
        this.outputDirectoryPath = valueContainer.getOutputDirectoryPath();
        this.bdioOutputDirectoryPath = valueContainer.getBdioOutputDirectoryPath();
        this.scanOutputDirectoryPath = valueContainer.getScanOutputDirectoryPath();
        this.searchDepth = valueContainer.getSearchDepth();
        this.loggingLevel = valueContainer.getLoggingLevel();
        this.defaultProjectVersionScheme = valueContainer.getDefaultProjectVersionScheme();
        this.defaultProjectVersionText = valueContainer.getDefaultProjectVersionText();
        this.defaultProjectVersionTimeformat = valueContainer.getDefaultProjectVersionTimeformat();
        this.aggregateBomName = valueContainer.getAggregateBomName();
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    // properties start
    private String detectProjectBomTool;

    private Integer bomToolSearchDepth;

    private Boolean bomToolContinueSearch;

    private String[] bomToolSearchExclusion;

    private Boolean bomToolSearchExclusionDefaults;

    private String excludedBomToolTypes;

    private String includedBomToolTypes;

    private Boolean failOnConfigWarning;

    private Boolean forceSuccess;

    private Boolean suppressConfigurationOutput;

    private Boolean suppressResultsOutput;

    private Boolean cleanupDetectFiles;

    private Boolean resolveTildeInPaths;

    private String sourcePath;

    private String outputDirectoryPath;

    private String bdioOutputDirectoryPath;

    private String scanOutputDirectoryPath;

    private Integer searchDepth;

    private String loggingLevel;

    private String defaultProjectVersionScheme;

    private String defaultProjectVersionText;

    private String defaultProjectVersionTimeformat;

    private String aggregateBomName;

    public int getBomToolSearchDepth() {
        return convertInt(bomToolSearchDepth);
    }

    public String getDetectProjectBomTool() {
        return detectProjectBomTool;
    }

    public Boolean getBomToolContinueSearch() {
        return BooleanUtils.toBoolean(bomToolContinueSearch);
    }

    public String[] getBomToolSearchExclusion() {
        return bomToolSearchExclusion;
    }

    public Boolean getBomToolSearchExclusionDefaults() {
        return BooleanUtils.toBoolean(bomToolSearchExclusionDefaults);
    }

    public String getExcludedBomToolTypes() {
        return excludedBomToolTypes == null ? null : excludedBomToolTypes.toUpperCase();
    }

    public String getIncludedBomToolTypes() {
        return includedBomToolTypes == null ? null : includedBomToolTypes.toUpperCase();
    }

    public boolean getCombineCodeLocations() {
        return false; //for now this is always false, in the future we could introduce a property.
    }

    public Boolean getCleanupDetectFiles() {
        return BooleanUtils.toBoolean(cleanupDetectFiles);
    }

    public boolean getResolveTildeInPaths() {
        return BooleanUtils.toBoolean(resolveTildeInPaths);
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getOutputDirectoryPath() {
        return outputDirectoryPath;
    }

    public String getBdioOutputDirectoryPath() {
        return bdioOutputDirectoryPath;
    }

    public String getScanOutputDirectoryPath() {
        return scanOutputDirectoryPath;
    }

    public int getSearchDepth() {
        return convertInt(searchDepth);
    }

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public Boolean getFailOnConfigWarning() {
        return BooleanUtils.toBoolean(failOnConfigWarning);
    }

    public boolean getSuppressConfigurationOutput() {
        return BooleanUtils.toBoolean(suppressConfigurationOutput);
    }

    public boolean getForceSuccess() {
        return BooleanUtils.toBoolean(forceSuccess);
    }

    public boolean getSuppressResultsOutput() {
        return BooleanUtils.toBoolean(suppressResultsOutput);
    }

    public String getDefaultProjectVersionScheme() {
        return defaultProjectVersionScheme == null ? null : defaultProjectVersionScheme.trim();
    }

    public String getDefaultProjectVersionText() {
        return defaultProjectVersionText == null ? null : defaultProjectVersionText.trim();
    }

    public String getDefaultProjectVersionTimeformat() {
        return defaultProjectVersionTimeformat == null ? null : defaultProjectVersionTimeformat.trim();
    }

    public String getAggregateBomName() {
        return aggregateBomName == null ? null : aggregateBomName.trim();
    }

    // properties end
}
