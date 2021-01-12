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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BdioCodeLocationCreator {
    private final Logger logger = LoggerFactory.getLogger(BdioCodeLocationCreator.class);

    private final CodeLocationNameManager codeLocationNameManager;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;

    public BdioCodeLocationCreator(final CodeLocationNameManager codeLocationNameManager, final DirectoryManager directoryManager,
        final EventSystem eventSystem) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
    }

    public BdioCodeLocationResult createFromDetectCodeLocations(final List<DetectCodeLocation> detectCodeLocations, String prefix, String suffix, final NameVersion projectNameVersion) throws DetectUserFriendlyException {
        final List<DetectCodeLocation> validDetectCodeLocations = findValidCodeLocations(detectCodeLocations);
        final Map<DetectCodeLocation, String> codeLocationsAndNames = createCodeLocationNameMap(validDetectCodeLocations, directoryManager.getSourceDirectory(), projectNameVersion, prefix, suffix);

        final Map<String, List<DetectCodeLocation>> codeLocationsByName = seperateCodeLocationsByName(codeLocationsAndNames);

        final List<BdioCodeLocation> bdioCodeLocations = createBdioCodeLocations(codeLocationsByName);

        return new BdioCodeLocationResult(bdioCodeLocations, codeLocationsAndNames);
    }

    private Map<DetectCodeLocation, String> createCodeLocationNameMap(final List<DetectCodeLocation> codeLocations, final File detectSourcePath, final NameVersion projectNameVersion, final String prefix,
        final String suffix) {
        final Map<DetectCodeLocation, String> nameMap = new HashMap<>();
        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            final String codeLocationName = codeLocationNameManager.createCodeLocationName(detectCodeLocation, detectSourcePath, projectNameVersion.getName(), projectNameVersion.getVersion(), prefix, suffix);
            nameMap.put(detectCodeLocation, codeLocationName);
        }
        return nameMap;
    }

    private List<DetectCodeLocation> findValidCodeLocations(final List<DetectCodeLocation> detectCodeLocations) {
        final List<DetectCodeLocation> validCodeLocations = new ArrayList<>();
        for (final DetectCodeLocation detectCodeLocation : detectCodeLocations) {
            if (detectCodeLocation.getDependencyGraph() == null) {
                logger.warn(String.format("Dependency graph is null for code location %s", detectCodeLocation.getSourcePath()));
                continue;
            }
            if (detectCodeLocation.getDependencyGraph().getRootDependencies().isEmpty()) {
                logger.warn(String.format("Could not find any dependencies for code location %s", detectCodeLocation.getSourcePath()));
            }
            validCodeLocations.add(detectCodeLocation);
        }
        return validCodeLocations;
    }

    private Map<String, List<DetectCodeLocation>> seperateCodeLocationsByName(final Map<DetectCodeLocation, String> detectCodeLocationNameMap) {
        final Map<String, List<DetectCodeLocation>> codeLocationNameMap = new HashMap<>();
        for (final Map.Entry<DetectCodeLocation, String> detectCodeLocationEntry : detectCodeLocationNameMap.entrySet()) {
            final String codeLocationName = detectCodeLocationEntry.getValue();
            if (!codeLocationNameMap.containsKey(codeLocationName)) {
                codeLocationNameMap.put(codeLocationName, new ArrayList<>());
            }
            codeLocationNameMap.get(codeLocationName).add(detectCodeLocationEntry.getKey());
        }
        return codeLocationNameMap;
    }

    private List<BdioCodeLocation> createBdioCodeLocations(final Map<String, List<DetectCodeLocation>> codeLocationsByName) {
        final List<BdioCodeLocation> bdioCodeLocations = new ArrayList<>();
        for (final Map.Entry<String, List<DetectCodeLocation>> codeLocationEntry : codeLocationsByName.entrySet()) {
            final String codeLocationName = codeLocationEntry.getKey();
            final List<DetectCodeLocation> codeLocations = codeLocationEntry.getValue();
            final List<BdioCodeLocation> transformedBdioCodeLocations = transformDetectCodeLocationsIntoBdioCodeLocations(codeLocations, codeLocationName);
            bdioCodeLocations.addAll(transformedBdioCodeLocations);
        }

        return bdioCodeLocations;
    }

    private List<BdioCodeLocation> transformDetectCodeLocationsIntoBdioCodeLocations(final List<DetectCodeLocation> codeLocations, final String codeLocationName) {
        final List<BdioCodeLocation> bdioCodeLocations;
        final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();

        if (codeLocations.size() > 1) {
            bdioCodeLocations = new ArrayList<>();
            for (int i = 0; i < codeLocations.size(); i++) {
                final DetectCodeLocation codeLocation = codeLocations.get(i);
                final String newCodeLocationName = String.format("%s %s", codeLocationName, Integer.toString(i));
                final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, newCodeLocationName, createBdioName(newCodeLocationName, integrationEscapeUtil));
                bdioCodeLocations.add(bdioCodeLocation);

            }
        } else if (codeLocations.size() == 1) {
            final DetectCodeLocation codeLocation = codeLocations.get(0);
            final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
            bdioCodeLocations = Collections.singletonList(bdioCodeLocation);
        } else {
            logger.error("Created a code location name but no code locations.");
            bdioCodeLocations = new ArrayList<>();
        }

        return bdioCodeLocations;
    }

    private String createBdioName(final String codeLocationName, final IntegrationEscapeUtil integrationEscapeUtil) {
        final String filenameRaw = StringUtils.replaceEach(codeLocationName, new String[] { "/", "\\", " " }, new String[] { "_", "_", "_" });
        return integrationEscapeUtil.replaceWithUnderscore(filenameRaw);
    }
}
