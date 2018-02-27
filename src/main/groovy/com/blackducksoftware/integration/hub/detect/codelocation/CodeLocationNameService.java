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
    private CodeLocationNameProvider1 codeLocationNameProvider1;

    @Autowired
    private CodeLocationNameProvider2 codeLocationNameProvider2;

    @Autowired
    private CodeLocationNameProvider3 codeLocationNameProvider3;

    public CodeLocationName createBomToolName(final String sourcePath, final String projectName, final String projectVersionName, final BomToolType bomToolType, final String prefix, final String suffix,
            final List<String> additionalNamePieces) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, bomToolType, sourcePath, null, prefix, suffix, additionalNamePieces, CodeLocationType.BOM);
        return codeLocationName;
    }

    public CodeLocationName createScanName(final String sourcePath, final String scanTargetPath, final String projectName, final String projectVersionName, final String prefix, final String suffix) {
        final CodeLocationName codeLocationName = new CodeLocationName(projectName, projectVersionName, null, sourcePath, scanTargetPath, prefix, suffix, null, CodeLocationType.SCAN);
        return codeLocationName;
    }

    public String generateBomToolCurrent(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final List<String> possiblePreviousCodeLocations = new ArrayList<>();
        possiblePreviousCodeLocations.add(codeLocationNameProvider1.generateBomToolName(codeLocationName));
        possiblePreviousCodeLocations.add(codeLocationNameProvider2.generateBomToolName(codeLocationName));
        possiblePreviousCodeLocations.add(codeLocationNameProvider3.generateBomToolName(codeLocationName));
        hubManager.manageExistingCodeLocations(possiblePreviousCodeLocations);

        return codeLocationNameProvider3.generateBomToolName(codeLocationName);
    }

    public String generateScanCurrent(final CodeLocationName codeLocationName) {
        // for any previously supported code location names, log if we find them
        final List<String> possiblePreviousCodeLocations = new ArrayList<>();
        possiblePreviousCodeLocations.add(codeLocationNameProvider1.generateScanName(codeLocationName));
        possiblePreviousCodeLocations.add(codeLocationNameProvider2.generateScanName(codeLocationName));
        possiblePreviousCodeLocations.add(codeLocationNameProvider3.generateScanName(codeLocationName));
        hubManager.manageExistingCodeLocations(possiblePreviousCodeLocations);

        return codeLocationNameProvider3.generateScanName(codeLocationName);
    }

}
