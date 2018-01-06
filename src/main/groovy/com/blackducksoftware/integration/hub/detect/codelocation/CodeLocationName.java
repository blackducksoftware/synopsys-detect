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
package com.blackducksoftware.integration.hub.detect.codelocation;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CodeLocationName {
    private final String projectName;
    private final String projectVersionName;
    private final BomToolType bomToolType;
    private final String sourcePath;
    private final String scanTargetPath;
    private final String prefix;
    private final String suffix;
    private final CodeLocationType codeLocationType;

    public CodeLocationName(final String projectName, final String projectVersionName, final BomToolType bomToolType, final String sourcePath, final String scanTargetPath, final String prefix, final String suffix,
            final CodeLocationType codeLocationType) {
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
        this.bomToolType = bomToolType;
        this.sourcePath = sourcePath;
        this.scanTargetPath = scanTargetPath;
        this.prefix = prefix;
        this.suffix = suffix;
        this.codeLocationType = codeLocationType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public BomToolType getBomToolType() {
        return bomToolType;
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getScanTargetPath() {
        return scanTargetPath;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public CodeLocationType getCodeLocationType() {
        return codeLocationType;
    }

}
