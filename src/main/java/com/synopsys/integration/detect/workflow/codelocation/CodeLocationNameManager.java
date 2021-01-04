/**
 * synopsys-detect
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
package com.synopsys.integration.detect.workflow.codelocation;

import java.io.File;

import org.springframework.lang.Nullable;

import com.synopsys.integration.util.NameVersion;

public class CodeLocationNameManager {
    private final CodeLocationNameGenerator codeLocationNameGenerator;

    public CodeLocationNameManager(CodeLocationNameGenerator codeLocationNameGenerator) {
        this.codeLocationNameGenerator = codeLocationNameGenerator;
    }

    public String createAggregateCodeLocationName(NameVersion projectNameVersion) {
        String aggregateCodeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            // The aggregate is exclusively used for the bdio and not the scans
            aggregateCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.BOM);
        } else {
            aggregateCodeLocationName = String.format("%s/%s Black Duck I/O Export", projectNameVersion.getName(), projectNameVersion.getVersion());
        }
        return aggregateCodeLocationName;
    }

    public String createCodeLocationName(DetectCodeLocation detectCodeLocation, File detectSourcePath, String projectName, String projectVersionName, String prefix, String suffix) {
        String codeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                codeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.DOCKER);
            } else {
                codeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameSourcedBom(detectCodeLocation);
            }
        } else {
            if (detectCodeLocation.getDockerImageName().isPresent()) {
                String dockerImage = detectCodeLocation.getDockerImageName().get();
                codeLocationName = codeLocationNameGenerator.createDockerCodeLocationName(detectCodeLocation.getSourcePath(), projectName, projectVersionName, dockerImage, prefix, suffix);
            } else {
                codeLocationName = codeLocationNameGenerator.createBomCodeLocationName(detectSourcePath, detectCodeLocation.getSourcePath(), projectName, projectVersionName, detectCodeLocation, prefix, suffix);
            }
        }
        return codeLocationName;
    }

    public String createScanCodeLocationName(File sourcePath, File scanTargetPath, @Nullable File dockerTar, String projectName, String projectVersionName, String prefix, String suffix) {
        String scanCodeLocationName;
        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN);
        } else if (dockerTar != null) {
            scanCodeLocationName = codeLocationNameGenerator.createDockerScanCodeLocationName(dockerTar, projectName, projectVersionName, prefix, suffix);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createScanCodeLocationName(sourcePath, scanTargetPath, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }

    public String createBinaryScanCodeLocationName(File targetFile, String projectName, String projectVersionName, String prefix, String suffix) {
        String scanCodeLocationName;

        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.SCAN);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createBinaryScanCodeLocationName(targetFile, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }

    public String createImpactAnalysisCodeLocationName(File sourceDirectory, String projectName, String projectVersionName, @Nullable String prefix, @Nullable String suffix) {
        String scanCodeLocationName;

        if (codeLocationNameGenerator.useCodeLocationOverride()) {
            scanCodeLocationName = codeLocationNameGenerator.getNextCodeLocationOverrideNameUnSourced(CodeLocationNameType.IMPACT_ANALYSIS);
        } else {
            scanCodeLocationName = codeLocationNameGenerator.createImpactAnalysisCodeLocationName(sourceDirectory, projectName, projectVersionName, prefix, suffix);
        }
        return scanCodeLocationName;
    }
}
