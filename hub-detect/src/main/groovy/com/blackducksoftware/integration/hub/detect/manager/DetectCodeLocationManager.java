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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfig;
import com.blackducksoftware.integration.hub.detect.configuration.HubConfig;
import com.blackducksoftware.integration.hub.detect.manager.result.codelocation.DetectCodeLocationResult;
import com.blackducksoftware.integration.hub.detect.model.BdioCodeLocation;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;

@Component
public class DetectCodeLocationManager {
    private final Logger logger = LoggerFactory.getLogger(DetectCodeLocationManager.class);

    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfig detectConfig;
    private final HubConfig hubConfig;

    @Autowired
    public DetectCodeLocationManager(final CodeLocationNameManager codeLocationNameManager, final DetectConfig detectConfig, final HubConfig hubConfig) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfig = detectConfig;
        this.hubConfig = hubConfig;
    }

    public DetectCodeLocationResult process(final List<DetectCodeLocation> detectCodeLocations, final String projectName, final String projectVersion) {
        final Set<BomToolType> failedBomTools = new HashSet<>();

        final String prefix = hubConfig.getProjectCodeLocationPrefix();
        final String suffix = hubConfig.getProjectCodeLocationSuffix();

        final List<DetectCodeLocation> validDetectCodeLocations = findValidCodeLocations(detectCodeLocations);
        final Map<DetectCodeLocation, String> codeLocationsAndNames = createCodeLocationNameMap(validDetectCodeLocations, detectConfig.getSourcePath(), projectName, projectVersion, prefix, suffix);
        final Map<String, List<DetectCodeLocation>> codeLocationsByName = seperateCodeLocationsByName(codeLocationsAndNames);

        final List<BdioCodeLocation> bdioCodeLocations = createBdioCodeLocations(codeLocationsByName, detectConfig.getSourceDirectory(), detectConfig.getCombineCodeLocations());

        // Sanity check that code location names are unique (they should be)
        final Map<String, List<BdioCodeLocation>> bdioByCodeLocationName = groupByCodeLocationNames(bdioCodeLocations);
        for (final String name : bdioByCodeLocationName.keySet()) {
            if (bdioByCodeLocationName.get(name).size() > 1) {
                logger.error("Multiple code locations were generated with the name: " + name);
                failedBomTools.addAll(getBomToolTypes(bdioByCodeLocationName.get(name)));
            }
        }

        // Sanity check that bdio file names are unique (they should be)
        final Map<String, List<BdioCodeLocation>> bdioByBdioName = groupByBdioNames(bdioCodeLocations);
        for (final String name : bdioByBdioName.keySet()) {
            if (bdioByBdioName.get(name).size() > 1) {
                logger.error("Multiple bdio names were generated with the name: " + name);
                failedBomTools.addAll(getBomToolTypes(bdioByBdioName.get(name)));
            }
        }

        final DetectCodeLocationResult result = new DetectCodeLocationResult(bdioCodeLocations, failedBomTools, codeLocationsAndNames);
        return result;
    }

    private Set<BomToolType> getBomToolTypes(final List<BdioCodeLocation> bdioCodeLocations) {
        return bdioCodeLocations.stream()
                .map(it -> it.codeLocation.getBomToolType())
                .collect(Collectors.toSet());
    }

    private Map<String, List<BdioCodeLocation>> groupByBdioNames(final List<BdioCodeLocation> bdioCodeLocations) {
        return bdioCodeLocations.stream().collect(Collectors.groupingBy(it -> it.bdioName, Collectors.toList()));
    }

    private Map<String, List<BdioCodeLocation>> groupByCodeLocationNames(final List<BdioCodeLocation> bdioCodeLocations) {
        return bdioCodeLocations.stream().collect(Collectors.groupingBy(it -> it.codeLocationName, Collectors.toList()));
    }

    private Map<DetectCodeLocation, String> createCodeLocationNameMap(final List<DetectCodeLocation> codeLocations, final String detectSourcePath, final String projectName, final String projectVersion, final String prefix,
            final String suffix) {
        final Map<DetectCodeLocation, String> nameMap = new HashMap<>();
        for (final DetectCodeLocation detectCodeLocation : codeLocations) {
            final String codeLocationName = codeLocationNameManager.createCodeLocationName(detectCodeLocation, detectSourcePath, projectName, projectVersion, prefix, suffix);
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
            if (detectCodeLocation.getDependencyGraph().getRootDependencies().size() <= 0) {
                logger.warn(String.format("Could not find any dependencies for code location %s", detectCodeLocation.getSourcePath()));
            }
            validCodeLocations.add(detectCodeLocation);
        }
        return validCodeLocations;
    }

    private Map<String, List<DetectCodeLocation>> seperateCodeLocationsByName(final Map<DetectCodeLocation, String> detectCodeLocationNameMap) {
        final Map<String, List<DetectCodeLocation>> codeLocationNameMap = new HashMap<>();
        for (final DetectCodeLocation detectCodeLocation : detectCodeLocationNameMap.keySet()) {
            final String codeLocationName = detectCodeLocationNameMap.get(detectCodeLocation);
            if (!codeLocationNameMap.containsKey(codeLocationName)) {
                codeLocationNameMap.put(codeLocationName, new ArrayList<DetectCodeLocation>());
            }
            codeLocationNameMap.get(codeLocationName).add(detectCodeLocation);
        }
        return codeLocationNameMap;
    }

    private String createBdioName(final String codeLocationName, final IntegrationEscapeUtil integrationEscapeUtil) {
        final String filenameRaw = StringUtils.replaceEach(codeLocationName, new String[] { "/", "\\", " " }, new String[] { "_", "_", "_" });
        final String filename = integrationEscapeUtil.escapeForUri(filenameRaw);
        return filename + ".jsonld";
    }

    private List<BdioCodeLocation> createBdioCodeLocations(final Map<String, List<DetectCodeLocation>> codeLocationsByName, final File sourcePath, final boolean combineCodeLocations) {
        final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();
        final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

        final List<BdioCodeLocation> bdioCodeLocations = new ArrayList<>();
        for (final String codeLocationName : codeLocationsByName.keySet()) {
            final List<DetectCodeLocation> codeLocationsForName = codeLocationsByName.get(codeLocationName);

            if (codeLocationsForName.size() > 1) {
                // we must either combine or create a unique name.
                if (combineCodeLocations) {
                    logger.info("Combining duplicate code locations with name: " + codeLocationName);
                    final MutableDependencyGraph combinedGraph = new MutableMapDependencyGraph();
                    final DetectCodeLocation copy = copyCodeLocation(codeLocationsForName.get(0), combinedGraph);
                    for (final DetectCodeLocation duplicate : codeLocationsForName) {
                        if (shouldCombine(logger, copy, duplicate)) {
                            combiner.addGraphAsChildrenToRoot(combinedGraph, duplicate.getDependencyGraph());
                        }
                    }
                    final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(copy, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
                    bdioCodeLocations.add(bdioCodeLocation);
                } else {
                    for (int i = 0; i < codeLocationsForName.size(); i++) {
                        final DetectCodeLocation codeLocation = codeLocationsForName.get(i);
                        final String suffix = " " + Integer.toString(i);
                        final String newCodeLocationName = codeLocationName + suffix;
                        final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, newCodeLocationName, createBdioName(newCodeLocationName, integrationEscapeUtil));
                        bdioCodeLocations.add(bdioCodeLocation);
                    }
                }
            } else if (codeLocationsForName.size() == 1) {
                final DetectCodeLocation codeLocation = codeLocationsForName.get(0);
                final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
                bdioCodeLocations.add(bdioCodeLocation);
            } else {
                logger.error("Created a code location name but no code locations.");
            }
        }

        return bdioCodeLocations;
    }

    private DetectCodeLocation copyCodeLocation(final DetectCodeLocation codeLocation, final DependencyGraph newGraph) {
        final DetectCodeLocation.Builder builder = new DetectCodeLocation.Builder(codeLocation.getBomToolType(), codeLocation.getSourcePath(), codeLocation.getExternalId(), newGraph);
        builder.dockerImage(codeLocation.getDockerImage());
        final DetectCodeLocation copy = builder.build();
        return copy;
    }

    private boolean shouldCombine(final Logger logger, final DetectCodeLocation codeLocationLeft, final DetectCodeLocation codeLocationRight) {
        if (codeLocationLeft.getBomToolType() != codeLocationRight.getBomToolType()) {
            logger.error("Cannot combine code locations with different bom tool types.");
            return false;
        }

        if (codeLocationLeft.getDockerImage() != codeLocationRight.getDockerImage()) {
            logger.error("Cannot combine code locations with different docker images.");
            return false;
        }

        if (codeLocationLeft.getSourcePath() != codeLocationRight.getSourcePath()) {
            logger.error("Cannot combine code locations with different source paths.");
            return false;
        }

        if (codeLocationLeft.getExternalId() != codeLocationRight.getExternalId()) {
            logger.error("Cannot combine code locations with different external ids.");
            return false;
        }

        return true;
    }

}
