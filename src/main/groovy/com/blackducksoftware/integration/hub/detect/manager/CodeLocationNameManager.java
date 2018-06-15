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

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.DockerCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.codelocation.ScanCodeLocationNameService;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;

@Component
public class CodeLocationNameManager {

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    private BomCodeLocationNameService bomCodeLocationNameService;

    @Autowired
    private DockerCodeLocationNameService dockerCodeLocationNameService;

    @Autowired
    private ScanCodeLocationNameService scanCodeLocationNameService;

    private int givenCodeLocationOverrideCount = 0;

    public boolean useCodeLocationOverride() {
        if (StringUtils.isNotBlank(detectConfiguration.getCodeLocationNameOverride())) {
            return true;
        } else {
            return false;
        }
    }

    public String getNextCodeLocationOverrideName() { //returns "override", then "override 2", then "override 3", etc
        givenCodeLocationOverrideCount++;
        final String base = detectConfiguration.getCodeLocationNameOverride();
        if (givenCodeLocationOverrideCount == 1) {
            return base;
        } else {
            final String codeLocationName = base + " " + Integer.toString(givenCodeLocationOverrideCount);
            return codeLocationName;
        }
    }

    public String createAggregateCodeLocationName() {
        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName();
        } else {
            return ""; //it is overridden in bdio creation later.
        }
    }

    public String createCodeLocationName(final DetectCodeLocation detectCodeLocation, final String detectSourcePath, final String projectName, final String projectVersionName,
            final String prefix, final String suffix) {

        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName();
        } else if (BomToolType.DOCKER == detectCodeLocation.getBomToolType()) {
            return dockerCodeLocationNameService.createCodeLocationName(detectCodeLocation.getSourcePath(), projectName, projectVersionName, detectCodeLocation.getDockerImage(), detectCodeLocation.getBomToolType(), prefix, suffix);
        } else {
            return bomCodeLocationNameService.createCodeLocationName(detectSourcePath, detectCodeLocation.getSourcePath(), detectCodeLocation.getExternalId(), detectCodeLocation.getBomToolType(), prefix, suffix);
        }
    }

    public String createScanCodeLocationName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        if (useCodeLocationOverride()) {
            return getNextCodeLocationOverrideName();
        } else {
            return scanCodeLocationNameService.createCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);
        }
    }

}
