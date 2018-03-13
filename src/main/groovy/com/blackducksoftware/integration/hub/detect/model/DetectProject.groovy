/*
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
package com.blackducksoftware.integration.hub.detect.model

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder

class DetectProject {
    private String projectName
    private String projectVersionName
    private List<DetectCodeLocation> detectCodeLocations = []

    public String getProjectName() {
        projectName
    }

    public String getProjectVersionName() {
        projectVersionName
    }

    /**
     * Only the DetectProjectManager should invoke this method.
     */
    public void setProjectName(String projectName) {
        this.projectName = projectName
    }

    /**
     * Only the DetectProjectManager should invoke this method.
     */
    public void setProjectVersionName(String projectVersionName) {
        this.projectVersionName = projectVersionName
    }

    public void setProjectNameIfNotSet(String projectName) {
        if (!this.projectName) {
            this.projectName = projectName
        }
    }
    public void setProjectVersionNameIfNotSet(String projectVersionName) {
        if (!this.projectVersionName) {
            this.projectVersionName = projectVersionName
        }
    }

    public void addAllDetectCodeLocations(List<DetectCodeLocation> detectCodeLocations) {
        detectCodeLocations.each { addDetectCodeLocation(it) }
    }

    public void addDetectCodeLocation(DetectCodeLocation detectCodeLocation) {
        setProjectNameIfNotSet(detectCodeLocation.bomToolProjectName)
        setProjectVersionNameIfNotSet(detectCodeLocation.bomToolProjectVersionName)

        detectCodeLocations.add(detectCodeLocation)
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        detectCodeLocations
    }

    public ProjectRequestBuilder createDefaultProjectRequestBuilder(DetectConfiguration detectConfiguration) {
        ProjectRequestBuilder builder = new ProjectRequestBuilder()
        builder.setProjectName(getProjectName())
        builder.setVersionName(getProjectVersionName())
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments())
        builder.setPhase(detectConfiguration.getProjectVersionPhase())
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution())
        builder.setProjectTier(detectConfiguration.getProjectTier())
        builder.setReleaseComments(detectConfiguration.getProjectVersionNotes())

        return builder
    }
}