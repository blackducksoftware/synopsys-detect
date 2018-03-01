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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.hub.HubManager;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

@Component
/**
 * Since consistency of code location names is extremely important, this class will maintain all current and all historic ways of creating code location names.
 */
public class CodeLocationNameService {
    @Autowired
    private HubManager hubManager;

    @Autowired
    private BomCodeLocationNameProvider1 bomCodeLocationNameProvider1;

    @Autowired
    private ScanCodeLocationNameProvider1 scanCodeLocationNameProvider1;

    @Autowired
    private BomCodeLocationNameProvider2 bomCodeLocationNameProvider2;

    @Autowired
    private ScanCodeLocationNameProvider2 scanCodeLocationNameProvider2;

    @Autowired
    private BomCodeLocationNameProvider3 bomCodeLocationNameProvider3;

    @Autowired
    private ScanCodeLocationNameProvider3 scanCodeLocationNameProvider3;

    @Autowired
    private DockerCodeLocationNameProvider1 dockerCodeLocationNameProvider1;

    public CodeLocationName createBomToolName(final String sourcePath, final String projectName, final String projectVersionName, final BomToolType bomToolType, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, null, bomToolType, sourcePath, null, prefix, suffix, CodeLocationType.BOM);
        return codeLocationName;
    }

    public CodeLocationName createScanName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, null, null, sourcePath, scanTargetPath, prefix, suffix, CodeLocationType.SCAN);
        return codeLocationName;
    }

    public CodeLocationName createDockerName(final String sourcePath, final String projectName, final String projectVersionName, final String dockerImage, final BomToolType bomToolType, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, dockerImage, bomToolType, sourcePath, null, prefix, suffix, CodeLocationType.DOCKER);
        return codeLocationName;
    }

    public String generateBomToolCurrent(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final List<String> possiblePreviousCodeLocations = new ArrayList<>();
        possiblePreviousCodeLocations.add(bomCodeLocationNameProvider1.generateName(codeLocationName));
        possiblePreviousCodeLocations.add(bomCodeLocationNameProvider2.generateName(codeLocationName));
        hubManager.manageExistingCodeLocations(possiblePreviousCodeLocations);

        return bomCodeLocationNameProvider3.generateName(codeLocationName);
    }

    public String generateScanCurrent(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final List<String> possiblePreviousCodeLocations = new ArrayList<>();
        possiblePreviousCodeLocations.add(scanCodeLocationNameProvider1.generateName(codeLocationName));
        possiblePreviousCodeLocations.add(scanCodeLocationNameProvider2.generateName(codeLocationName));
        hubManager.manageExistingCodeLocations(possiblePreviousCodeLocations);

        return scanCodeLocationNameProvider3.generateName(codeLocationName);
    }

    public String generateDockerCurrent(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final List<String> possiblePreviousCodeLocations = new ArrayList<>();
        possiblePreviousCodeLocations.add(bomCodeLocationNameProvider1.generateName(codeLocationName));
        possiblePreviousCodeLocations.add(bomCodeLocationNameProvider2.generateName(codeLocationName));
        possiblePreviousCodeLocations.add(bomCodeLocationNameProvider3.generateName(codeLocationName));
        hubManager.manageExistingCodeLocations(possiblePreviousCodeLocations);

        return dockerCodeLocationNameProvider1.generateName(codeLocationName);
    }

}
