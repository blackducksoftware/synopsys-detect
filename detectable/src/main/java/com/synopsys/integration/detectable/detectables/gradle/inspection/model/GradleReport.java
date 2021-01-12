/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import java.util.ArrayList;
import java.util.List;

public class GradleReport {
    private String projectSourcePath = "";
    private String projectGroup = "";
    private String projectName = "";
    private String projectVersionName = "";
    private List<GradleConfiguration> configurations = new ArrayList<>();

    public String getProjectSourcePath() {
        return projectSourcePath;
    }

    public void setProjectSourcePath(final String projectSourcePath) {
        this.projectSourcePath = projectSourcePath;
    }

    public String getProjectGroup() {
        return projectGroup;
    }

    public void setProjectGroup(final String projectGroup) {
        this.projectGroup = projectGroup;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(final String projectName) {
        this.projectName = projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public void setProjectVersionName(final String projectVersionName) {
        this.projectVersionName = projectVersionName;
    }

    public List<GradleConfiguration> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(final List<GradleConfiguration> configurations) {
        this.configurations = configurations;
    }
}
