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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
public class CodeLocationNameService {
    @Autowired
    private BomCodeLocationNameProvider bomCodeLocationNameProvider;

    @Autowired
    private ScanCodeLocationNameProvider scanCodeLocationNameProvider;

    @Autowired
    private DockerCodeLocationNameProvider dockerCodeLocationNameProvider;

    public String createBomToolName(final String sourcePath, final String projectName, final String projectVersionName, final BomToolType bomToolType, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, null, bomToolType, sourcePath, null, prefix, suffix, CodeLocationType.BOM);
        return bomCodeLocationNameProvider.generateName(codeLocationName);
    }

    public String createScanName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, null, null, sourcePath, scanTargetPath, prefix, suffix, CodeLocationType.SCAN);
        return scanCodeLocationNameProvider.generateName(codeLocationName);
    }

    public String createDockerName(final String sourcePath, final String projectName, final String projectVersionName, final String dockerImage, final BomToolType bomToolType, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, dockerImage, bomToolType, sourcePath, null, prefix, suffix, CodeLocationType.DOCKER);
        return dockerCodeLocationNameProvider.generateName(codeLocationName);
    }

}
