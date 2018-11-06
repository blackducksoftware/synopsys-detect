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
package com.blackducksoftware.integration.hub.detect.workflow.codelocation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.workflow.event.Event;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.hub.bdio.graph.DependencyGraph;
import com.synopsys.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.util.IntegrationEscapeUtil;
import com.synopsys.integration.util.NameVersion;

public class BdioCodeLocationCreator {
    private final Logger logger = LoggerFactory.getLogger(BdioCodeLocationCreator.class);

    private final CodeLocationNameManager codeLocationNameManager;
    private final DetectConfiguration detectConfiguration;
    private final DirectoryManager directoryManager;
    private final EventSystem eventSystem;

    public BdioCodeLocationCreator(final CodeLocationNameManager codeLocationNameManager, final DetectConfiguration detectConfiguration, final DirectoryManager directoryManager,
        final EventSystem eventSystem) {
        this.codeLocationNameManager = codeLocationNameManager;
        this.detectConfiguration = detectConfiguration;
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
    }

    public BdioCodeLocationResult createFromDetectCodeLocations(final List<DetectCodeLocation> detectCodeLocations, NameVersion projectNameVersion) {
        final Set<DetectorType> failedBomToolGroups = new HashSet<>();

        final String prefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.None);
        final String suffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.None);

        final List<DetectCodeLocation> validDetectCodeLocations = findValidCodeLocations(detectCodeLocations);
        final Map<DetectCodeLocation, String> codeLocationsAndNames = createCodeLocationNameMap(validDetectCodeLocations, directoryManager.getSourceDirectory().getAbsolutePath(), projectNameVersion, prefix, suffix);
        final Map<String, List<DetectCodeLocation>> codeLocationsByName = seperateCodeLocationsByName(codeLocationsAndNames);

        // We can create a DetectProperty to combine duplicate code location names in the future if users want that
        final List<BdioCodeLocation> bdioCodeLocations = createBdioCodeLocations(codeLocationsByName, directoryManager.getSourceDirectory(), false);

        // Sanity check that code location names are unique (they should be)
        final Map<String, List<BdioCodeLocation>> bdioByCodeLocationName = groupByCodeLocationNames(bdioCodeLocations);
        for (final String name : bdioByCodeLocationName.keySet()) {
            if (bdioByCodeLocationName.get(name).size() > 1) {
                logger.error("Multiple code locations were generated with the name: " + name);
                failedBomToolGroups.addAll(getBomToolGroupTypes(bdioByCodeLocationName.get(name)));
            }
        }

        // Sanity check that bdio file names are unique (they should be)
        final Map<String, List<BdioCodeLocation>> bdioByBdioName = groupByBdioNames(bdioCodeLocations);
        for (final String name : bdioByBdioName.keySet()) {
            if (bdioByBdioName.get(name).size() > 1) {
                logger.error("Multiple bdio names were generated with the name: " + name);
                failedBomToolGroups.addAll(getBomToolGroupTypes(bdioByBdioName.get(name)));
            }
        }

        final BdioCodeLocationResult result = new BdioCodeLocationResult(bdioCodeLocations, failedBomToolGroups, codeLocationsAndNames);
        eventSystem.publishEvent(Event.CodeLocationsCalculated, result);
        return result;
    }

    private Set<DetectorType> getBomToolGroupTypes(final List<BdioCodeLocation> bdioCodeLocations) {
        return bdioCodeLocations.stream()
                   .map(it -> it.codeLocation.getDetectorType())
                   .collect(Collectors.toSet());
    }

    private Map<String, List<BdioCodeLocation>> groupByBdioNames(final List<BdioCodeLocation> bdioCodeLocations) {
        return bdioCodeLocations.stream().collect(Collectors.groupingBy(it -> it.bdioName, Collectors.toList()));
    }

    private Map<String, List<BdioCodeLocation>> groupByCodeLocationNames(final List<BdioCodeLocation> bdioCodeLocations) {
        return bdioCodeLocations.stream().collect(Collectors.groupingBy(it -> it.codeLocationName, Collectors.toList()));
    }

    private Map<DetectCodeLocation, String> createCodeLocationNameMap(final List<DetectCodeLocation> codeLocations, final String detectSourcePath, NameVersion projectNameVersion, final String prefix,
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

    private List<BdioCodeLocation> createBdioCodeLocations(final Map<String, List<DetectCodeLocation>> codeLocationsByName, final File sourcePath, final boolean combineCodeLocations) {
        final List<BdioCodeLocation> bdioCodeLocations = new ArrayList<>();

        for (final String codeLocationName : codeLocationsByName.keySet()) {
            final List<DetectCodeLocation> codeLocations = codeLocationsByName.get(codeLocationName);
            final List<BdioCodeLocation> transformedBdioCodeLocations = transformDetectCodeLocationsIntoBdioCodeLocations(codeLocations, codeLocationName, combineCodeLocations);
            bdioCodeLocations.addAll(transformedBdioCodeLocations);
        }

        return bdioCodeLocations;
    }

    private List<BdioCodeLocation> transformDetectCodeLocationsIntoBdioCodeLocations(final List<DetectCodeLocation> codeLocations, final String codeLocationName, final boolean combineCodeLocations) {
        final List<BdioCodeLocation> bdioCodeLocations;
        final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();

        if (codeLocations.size() > 1) {
            // we must either combine or create a unique name.
            if (combineCodeLocations) {
                final DependencyGraphCombiner combiner = new DependencyGraphCombiner();
                logger.info("Combining duplicate code locations with name: " + codeLocationName);
                final MutableDependencyGraph combinedGraph = new MutableMapDependencyGraph();
                final DetectCodeLocation finalCodeLocation = copyCodeLocation(codeLocations.get(0), combinedGraph);
                codeLocations.stream()
                    .filter(codeLocation -> shouldCombine(logger, finalCodeLocation, codeLocation))
                    .forEach(codeLocation -> combiner.addGraphAsChildrenToRoot(combinedGraph, codeLocation.getDependencyGraph()));

                final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(finalCodeLocation, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
                bdioCodeLocations = Arrays.asList(bdioCodeLocation);
            } else {
                bdioCodeLocations = new ArrayList<>();
                for (int i = 0; i < codeLocations.size(); i++) {
                    final DetectCodeLocation codeLocation = codeLocations.get(i);
                    final String newCodeLocationName = String.format("%s %s", codeLocationName, Integer.toString(i));
                    final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, newCodeLocationName, createBdioName(newCodeLocationName, integrationEscapeUtil));
                    bdioCodeLocations.add(bdioCodeLocation);
                }
            }
        } else if (codeLocations.size() == 1) {
            final DetectCodeLocation codeLocation = codeLocations.get(0);
            final BdioCodeLocation bdioCodeLocation = new BdioCodeLocation(codeLocation, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
            bdioCodeLocations = Arrays.asList(bdioCodeLocation);
        } else {
            logger.error("Created a code location name but no code locations.");
            bdioCodeLocations = new ArrayList<>();
        }

        return bdioCodeLocations;
    }

    private String createBdioName(final String codeLocationName, final IntegrationEscapeUtil integrationEscapeUtil) {
        final String filenameRaw = StringUtils.replaceEach(codeLocationName, new String[] { "/", "\\", " " }, new String[] { "_", "_", "_" });
        final String filename = integrationEscapeUtil.escapeForUri(filenameRaw);
        return filename + ".jsonld";
    }

    private DetectCodeLocation copyCodeLocation(final DetectCodeLocation codeLocation, final DependencyGraph newGraph) {
        final DetectCodeLocation.Builder builder = new DetectCodeLocation.Builder(codeLocation.getDetectorType(), codeLocation.getSourcePath(), codeLocation.getExternalId(), newGraph);
        builder.dockerImage(codeLocation.getDockerImage());
        final DetectCodeLocation copy = builder.build();
        return copy;
    }

    private boolean shouldCombine(final Logger logger, final DetectCodeLocation codeLocationLeft, final DetectCodeLocation codeLocationRight) {
        if (codeLocationLeft.getDetectorType() != codeLocationRight.getDetectorType()) {
            logger.error("Cannot combine code locations with different detector types.");
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
