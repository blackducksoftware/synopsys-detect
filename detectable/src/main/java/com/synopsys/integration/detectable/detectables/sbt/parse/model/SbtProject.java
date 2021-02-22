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
package com.synopsys.integration.detectable.detectables.sbt.parse.model;

import java.util.List;

import com.synopsys.integration.bdio.model.externalid.ExternalId;

public class SbtProject {
    private String projectName;
    private String projectVersion;
    private ExternalId projectExternalId;
    private List<SbtDependencyModule> modules;

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

    public ExternalId getProjectExternalId() {
        return projectExternalId;
    }

    public void setProjectExternalId(final ExternalId projectExternalId) {
        this.projectExternalId = projectExternalId;
    }

    public List<SbtDependencyModule> getModules() {
        return modules;
    }

    public void setModules(final List<SbtDependencyModule> modules) {
        this.modules = modules;
    }
}
