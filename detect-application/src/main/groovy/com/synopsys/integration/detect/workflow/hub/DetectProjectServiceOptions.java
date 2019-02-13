/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.workflow.hub;

public class DetectProjectServiceOptions {
    private final String projectVersionPhase;
    private final String projectVersionDistribution;
    private final Integer projectTier;
    private final String projectDescription;
    private final String projectVersionNotes;
    private final String[] cloneCategories;
    private final Boolean projectLevelAdjustments;
    private final Boolean forceProjectVersionUpdate;
    private final String cloneVersionName;
    private final String projectVersionNickname;
    private final String applicationId;

    public DetectProjectServiceOptions(final String projectVersionPhase, final String projectVersionDistribution, final Integer projectTier, final String projectDescription, final String projectVersionNotes,
        final String[] cloneCategories, final Boolean projectLevelAdjustments, final Boolean forceProjectVersionUpdate, final String cloneVersionName, final String projectVersionNickname, final String applicationId) {
        this.projectVersionPhase = projectVersionPhase;
        this.projectVersionDistribution = projectVersionDistribution;
        this.projectTier = projectTier;
        this.projectDescription = projectDescription;
        this.projectVersionNotes = projectVersionNotes;
        this.cloneCategories = cloneCategories;
        this.projectLevelAdjustments = projectLevelAdjustments;
        this.forceProjectVersionUpdate = forceProjectVersionUpdate;
        this.cloneVersionName = cloneVersionName;
        this.projectVersionNickname = projectVersionNickname;
        this.applicationId = applicationId;
    }

    public String getProjectVersionPhase() {
        return projectVersionPhase;
    }

    public String getProjectVersionDistribution() {
        return projectVersionDistribution;
    }

    public Integer getProjectTier() {
        return projectTier;
    }

    public String getProjectDescription() {
        return projectDescription;
    }

    public String getProjectVersionNotes() {
        return projectVersionNotes;
    }

    public String[] getCloneCategories() {
        return cloneCategories;
    }

    public Boolean isProjectLevelAdjustments() {
        return projectLevelAdjustments;
    }

    public Boolean isForceProjectVersionUpdate() {
        return forceProjectVersionUpdate;
    }

    public String getCloneVersionName() {
        return cloneVersionName;
    }

    public String getProjectVersionNickname() {
        return projectVersionNickname;
    }

    public String getApplicationId() {
        return applicationId;
    }
}
