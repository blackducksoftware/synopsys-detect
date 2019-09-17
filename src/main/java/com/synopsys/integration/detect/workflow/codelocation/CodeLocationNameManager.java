/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.util.NameVersion;

public class CodeLocationNameManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final CodeLocationNameGenerator codeLocationNameGenerator;

    public CodeLocationNameManager(final CodeLocationNameGenerator codeLocationNameGenerator) {
        this.codeLocationNameGenerator = codeLocationNameGenerator;
    }

    public String createAggregateCodeLocationName(final NameVersion projectNameVersion) {
        final String aggregateCodeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            // The aggregate is exclusively used for the bdio and not the scans
            aggregateCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.BOM);
        } else {
            aggregateCodeLocationName = String.format("%s/%s Black Duck I/O Export", projectNameVersion.getName(), projectNameVersion.getVersion());
        }
        return aggregateCodeLocationName;
    }

    public String createCodeLocationName(final DetectCodeLocation detectCodeLocation, final String detectSourcePath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String codeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                codeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.DOCKER);
            } else {
                codeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameSourcedBom(detectCodeLocation);
            }
        } else {
            String sourcePath;
            try {
                sourcePath = detectCodeLocation.getSourcePath().getCanonicalPath();
            } catch (final IOException e) {
                sourcePath = detectCodeLocation.getSourcePath().toString();
            }
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                final String dockerImage = detectCodeLocation.getDockerImageName().get();
                codeLocationName = codeLocationNameGenerator.createDockerCodeLocationName(sourcePath, projectName, projectVersionName, dockerImage, prefix, suffix);
            } else {
                codeLocationName = codeLocationNameGenerator.createBomCodeLocationName(detectSourcePath, sourcePath, projectName, projectVersionName, detectCodeLocation, prefix, suffix);
            }
        }
        return codeLocationName;
    }

    public String createScanCodeLocationName(final String sourcePath, final String scanTargetPath, final String dockerTarFilename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String scanCodeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN);
        } else if (StringUtils.isNotBlank(dockerTarFilename)) {
            scanCodeLocationName = codeLocationNameGenerator.createDockerScanCodeLocationName(dockerTarFilename, projectName, projectVersionName, prefix, suffix);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createScanCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }

    public String createBinaryScanCodeLocationName(final String filename, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final String scanCodeLocationName;

        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createBinaryScanCodeLocationName(filename, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }
}
