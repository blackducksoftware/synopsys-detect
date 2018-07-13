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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;

public class GradleParseResult {
    private String projectName;
    private String projectVersion;
    private DetectCodeLocation codeLocation;

    public GradleParseResult(final String projectName, final String projectVersion, final DetectCodeLocation codeLocation) {
        this.setProjectName(projectName);
        this.setProjectVersion(projectVersion);
        this.setCodeLocation(codeLocation);
    }

    public boolean hasProjectNameAndVersion() {
        return StringUtils.isNotBlank(projectName) && StringUtils.isNotBlank(projectVersion);
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

    public DetectCodeLocation getCodeLocation() {
        return codeLocation;
    }

    public void setCodeLocation(final DetectCodeLocation codeLocation) {
        this.codeLocation = codeLocation;
    }
}
