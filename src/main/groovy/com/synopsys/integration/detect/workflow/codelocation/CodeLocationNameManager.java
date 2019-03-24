/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.codelocation;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.util.NameVersion;

public class CodeLocationNameManager {
    private final DetectConfiguration detectConfiguration;
    private final CodeLocationNameGenerator codeLocationNameGenerator;
    private final Map<String, Integer> nameCounters = new HashMap<>();

    public CodeLocationNameManager(final DetectConfiguration detectConfiguration, final CodeLocationNameGenerator codeLocationNameGenerator) {
        this.detectConfiguration = detectConfiguration;
        this.codeLocationNameGenerator = codeLocationNameGenerator;
    }

    public String createAggregateCodeLocationName(final NameVersion projectNameVersion) {
        final String aggregateCodeLocationName;
        if (useCodeLocationOverride()) {
            // The aggregate is exclusively used for the bdio and not the scans
            aggregateCodeLocationName = getNextCodeLocationOverrideName(CodeLocationNameType.BOM);
        } else {
            aggregateCodeLocationName = String.format("%s/%s Black Duck I/O Export", projectNameVersion.getName(), projectNameVersion.getVersion());
        }
        return aggregateCodeLocationName;
    }

    public String createCodeLocationName(final DetectCodeLocation detectCodeLocation, final String detectSourcePath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String codeLocationName;
        if (useCodeLocationOverride()) {
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                codeLocationName = getNextCodeLocationOverrideName(CodeLocationNameType.DOCKER);
            } else {
                codeLocationName = getNextCodeLocationOverrideName(CodeLocationNameType.BOM);
            }
        } else {
            String sourcePath = detectCodeLocation.getSourcePath().toString();
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                String dockerImage = detectCodeLocation.getDockerImageName().get();
                codeLocationName = codeLocationNameGenerator.createDockerCodeLocationName(sourcePath, projectName, projectVersionName, dockerImage, prefix, suffix);
            } else {
                String creator = detectCodeLocation.getCreatorName().orElse("detect");
                codeLocationName = codeLocationNameGenerator.createBomCodeLocationName(detectSourcePath, sourcePath, detectCodeLocation.getExternalId(), creator, prefix, suffix);
            }
        }
        return codeLocationName;
    }

    public String createScanCodeLocationName(final String sourcePath, final String scanTargetPath, final String dockerTarFilename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String scanCodeLocationName;

        if (useCodeLocationOverride()) {
            scanCodeLocationName = getNextCodeLocationOverrideName(CodeLocationNameType.SCAN);
        } else if (StringUtils.isNotBlank(dockerTarFilename)) {
            scanCodeLocationName = codeLocationNameGenerator.createDockerScanCodeLocationName(dockerTarFilename, projectName, projectVersionName, prefix, suffix);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createScanCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }

    public String createBinaryScanCodeLocationName(final String filename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String scanCodeLocationName;

        if (useCodeLocationOverride()) {
            scanCodeLocationName = getNextCodeLocationOverrideName(CodeLocationNameType.SCAN);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createBinaryScanCodeLocationName(filename, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }

    private boolean useCodeLocationOverride() {
        return StringUtils.isNotBlank(detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None));
    }

    private String getNextCodeLocationOverrideName(final CodeLocationNameType codeLocationNameType) { // returns "override", then "override 2", then "override 3", etc
        final String baseName = detectConfiguration.getProperty(DetectProperty.DETECT_CODE_LOCATION_NAME, PropertyAuthority.None) + " " + codeLocationNameType.name();
        final int nameIndex = deriveNameIndex(baseName);
        final String nextName = deriveCodeLocationName(baseName, nameIndex);
        return nextName;
    }

    private String deriveCodeLocationName(final String baseName, final int nameIndex) {
        final String nextName;
        if (nameIndex > 0) {
            nextName = baseName + " " + nameIndex;
        } else {
            nextName = baseName;
        }
        return nextName;
    }

    private int deriveNameIndex(final String baseName) {
        int nameIndex;
        if (nameCounters.containsKey(baseName)) {
            nameIndex = nameCounters.get(baseName);
            nameIndex++;
        } else {
            nameIndex = 0;
        }
        nameCounters.put(baseName, nameIndex);
        return nameIndex;
    }

}
