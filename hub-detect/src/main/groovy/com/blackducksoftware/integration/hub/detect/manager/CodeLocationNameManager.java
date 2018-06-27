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
package com.blackducksoftware.integration.hub.detect.manager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.CodeLocationType;
import com.blackducksoftware.integration.hub.detect.codelocation.DockerCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.DockerScanCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class CodeLocationNameManager {
    private final DetectConfigWrapper detectConfigWrapper;
    private final BomCodeLocationNameService bomCodeLocationNameService;
    private final DockerCodeLocationNameService dockerCodeLocationNameService;
    private final ScanCodeLocationNameService scanCodeLocationNameService;
    private final DockerScanCodeLocationNameService dockerScanCodeLocationNameService;

    private int givenCodeLocationOverrideCount = 0;

    @Autowired
    public CodeLocationNameManager(final DetectConfigWrapper detectConfigWrapper, final BomCodeLocationNameService bomCodeLocationNameService,
            final DockerCodeLocationNameService dockerCodeLocationNameService, final ScanCodeLocationNameService scanCodeLocationNameService,
            final DockerScanCodeLocationNameService dockerScanCodeLocationNameService) {
        this.detectConfigWrapper = detectConfigWrapper;
        this.bomCodeLocationNameService = bomCodeLocationNameService;
        this.dockerCodeLocationNameService = dockerCodeLocationNameService;
        this.scanCodeLocationNameService = scanCodeLocationNameService;
        this.dockerScanCodeLocationNameService = dockerScanCodeLocationNameService;

    }

    private boolean useCodeLocationOverride() {
        if (StringUtils.isNotBlank(detectConfigWrapper.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME))) {
            return true;
        } else {
            return false;
        }
    }

    private String getNextCodeLocationOverrideName(CodeLocationType codeLocationType) { //returns "override", then "override 2", then "override 3", etc
        givenCodeLocationOverrideCount++;
        final String base = detectConfigWrapper.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME) + " " + codeLocationType.name();
        if (givenCodeLocationOverrideCount == 1) {
            return base;
        } else {
            final String codeLocationName = base + " " + Integer.toString(givenCodeLocationOverrideCount);
            return codeLocationName;
        }
    }

    public String createAggregateCodeLocationName() {
        if (useCodeLocationOverride()) {
            //The aggregate is exclusively used for the bdio and not the scans
            return getNextCodeLocationOverrideName(CodeLocationType.BOM);
        } else {
            return ""; //it is overridden in bdio creation later.
        }
    }

    public String createCodeLocationName(final DetectCodeLocation detectCodeLocation, final String detectSourcePath, final String projectName, final String projectVersionName,
            final String prefix, final String suffix) {

        if (useCodeLocationOverride()) {
            if (BomToolGroupType.DOCKER == detectCodeLocation.getBomToolType()) {
                return getNextCodeLocationOverrideName(CodeLocationType.DOCKER);
            } else {
                return getNextCodeLocationOverrideName(CodeLocationType.BOM);
            }
        } else if (BomToolGroupType.DOCKER == detectCodeLocation.getBomToolType()) {
            return dockerCodeLocationNameService.createCodeLocationName(detectCodeLocation.getSourcePath(), projectName, projectVersionName, detectCodeLocation.getDockerImage(), detectCodeLocation.getBomToolType(), prefix, suffix);
        } else {
            return bomCodeLocationNameService.createCodeLocationName(detectSourcePath, detectCodeLocation.getSourcePath(), detectCodeLocation.getExternalId(), detectCodeLocation.getBomToolType(), prefix, suffix);
        }
    }

    public String createScanCodeLocationName(final String sourcePath, final String scanTargetPath, String dockerTarFilename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName(CodeLocationType.SCAN);
        } else {
            if (StringUtils.isNotBlank(dockerTarFilename)) {
                return dockerScanCodeLocationNameService.createCodeLocationName(dockerTarFilename, projectName, projectVersionName, prefix, suffix);
            } else {
                return scanCodeLocationNameService.createCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);
            }
        }
    }

}
