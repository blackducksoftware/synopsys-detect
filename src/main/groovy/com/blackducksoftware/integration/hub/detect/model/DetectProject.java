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
package com.blackducksoftware.integration.hub.detect.model;

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

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.codelocation.BomCodeLocationNameFactory;
import com.blackducksoftware.integration.hub.detect.codelocation.DockerCodeLocationNameFactory;
import com.blackducksoftware.integration.hub.service.model.ProjectRequestBuilder;
import com.blackducksoftware.integration.util.IntegrationEscapeUtil;

public class DetectProject {
    private final List<ProcessedDetectCodeLocation> processedDetectCodeLocations = new ArrayList<>();
    private final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();
    private final Set<BomToolType> failedBomTools = new HashSet<>();

    private String projectName;
    private String projectVersionName;
    private String codeLocationNamePrefix;
    private String codeLocationNameSuffix;

    /**
     * Only the DetectProjectManager should invoke this method.
     */
    public void setProjectDetails(final String projectName, final String projectVersionName, final String codeLocationNamePrefix, final String codeLocationNameSuffix) {
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
        this.codeLocationNamePrefix = codeLocationNamePrefix;
        this.codeLocationNameSuffix = codeLocationNameSuffix;
    }

    public void setProjectNameIfNotSet(final String projectName) {
        if (StringUtils.isBlank(this.projectName)) {
            this.projectName = projectName;
        }
    }

    public void setProjectVersionNameIfNotSet(final String projectVersionName) {
        if (StringUtils.isBlank(this.projectVersionName)) {
            this.projectVersionName = projectVersionName;
        }
    }

    public void addAllDetectCodeLocations(final List<DetectCodeLocation> detectCodeLocations) {
        detectCodeLocations
        .stream()
        .forEach(it -> addDetectCodeLocation(it));
    }

    public void addDetectCodeLocation(final DetectCodeLocation detectCodeLocation) {
        detectCodeLocations.add(detectCodeLocation);
    }

    public List<DetectCodeLocation> getDetectCodeLocations() {
        return detectCodeLocations;
    }

    public ProjectRequestBuilder createDefaultProjectRequestBuilder(final DetectConfiguration detectConfiguration) {
        final ProjectRequestBuilder builder = new ProjectRequestBuilder();
        builder.setProjectName(getProjectName());
        builder.setVersionName(getProjectVersionName());
        builder.setProjectLevelAdjustments(detectConfiguration.getProjectLevelMatchAdjustments());
        builder.setPhase(detectConfiguration.getProjectVersionPhase());
        builder.setDistribution(detectConfiguration.getProjectVersionDistribution());
        builder.setProjectTier(detectConfiguration.getProjectTier());
        builder.setReleaseComments(detectConfiguration.getProjectVersionNotes());

        return builder;
    }

    private Map<String, List<DetectCodeLocation>> seperateCodeLocationsByName(final BomCodeLocationNameFactory bomCodeLocationNameFactory, final DockerCodeLocationNameFactory dockerCodeLocationNameFactory, final String detectSourcePath, final Logger logger) {
        final Map<String, List<DetectCodeLocation>> codeLocationNameMap = new HashMap<>();
        for (final DetectCodeLocation detectCodeLocation : getDetectCodeLocations()) {
            if (detectCodeLocation.getDependencyGraph() == null) {
                logger.warn(String.format("Dependency graph is null for code location %s", detectCodeLocation.getSourcePath()));
                continue;
            }
            if (detectCodeLocation.getDependencyGraph().getRootDependencies().size() <= 0) {
                logger.warn(String.format("Could not find any dependencies for code location %s", detectCodeLocation.getSourcePath()));
            }

            final String codeLocationName = detectCodeLocation.createCodeLocationName(bomCodeLocationNameFactory, dockerCodeLocationNameFactory, detectSourcePath, projectName, projectVersionName, getCodeLocationNamePrefix(), getCodeLocationNameSuffix());

            if (!codeLocationNameMap.containsKey(codeLocationName)) {
                codeLocationNameMap.put(codeLocationName, new ArrayList<DetectCodeLocation>());
            }

            codeLocationNameMap.get(codeLocationName).add(detectCodeLocation);
        }

        return codeLocationNameMap;
    }

    private String createBdioName(final String codeLocationName, final IntegrationEscapeUtil integrationEscapeUtil) {
        final String filenameRaw = StringUtils.replaceEach(codeLocationName, new String[] {"/", "\\", " "}, new String[] {"_", "_", "_"});
        final String filename = integrationEscapeUtil.escapeForUri(filenameRaw);
        return filename;
    }

    public void processDetectCodeLocations(final BomCodeLocationNameFactory bomCodeLocationNameFactory, final DockerCodeLocationNameFactory dockerCodeLocationNameFactory, final String detectSourcePath, final Logger logger, final File sourcePath, final boolean combineCodeLocations) {
        final IntegrationEscapeUtil integrationEscapeUtil = new IntegrationEscapeUtil();
        final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

        final Map<String, List<DetectCodeLocation>> codeLocationsByName = seperateCodeLocationsByName(bomCodeLocationNameFactory, dockerCodeLocationNameFactory, detectSourcePath, logger);

        for (final String codeLocationName : codeLocationsByName.keySet()) {
            final List<DetectCodeLocation> codeLocationsForName = codeLocationsByName.get(codeLocationName);

            if (codeLocationsForName.size() > 1) {
                //we must either combine or create a unique name.
                if (combineCodeLocations) {
                    logger.info("Combining duplicate code locations with name: " + codeLocationName);
                    final MutableDependencyGraph combinedGraph = new MutableMapDependencyGraph();
                    final DetectCodeLocation copy = copyCodeLocation(codeLocationsForName.get(0), combinedGraph);
                    for (final DetectCodeLocation duplicate : codeLocationsForName) {
                        if (shouldCombine(logger, copy, duplicate)) {
                            combiner.addGraphAsChildrenToRoot(combinedGraph, duplicate.getDependencyGraph());
                        }
                    }
                    final ProcessedDetectCodeLocation processedCodeLocation = new ProcessedDetectCodeLocation(copy, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
                    processedDetectCodeLocations.add(processedCodeLocation);
                }else {
                    for (int i = 0; i < codeLocationsForName.size(); i++) {
                        final DetectCodeLocation codeLocation = codeLocationsForName.get(i);
                        final String suffix = " " + Integer.toString(i);
                        final ProcessedDetectCodeLocation processedCodeLocation = new ProcessedDetectCodeLocation(codeLocation, codeLocationName + suffix, createBdioName(codeLocationName, integrationEscapeUtil) + suffix);
                        processedDetectCodeLocations.add(processedCodeLocation);
                    }
                }
            } else if (codeLocationsForName.size() == 1){
                final DetectCodeLocation codeLocation = codeLocationsForName.get(0);
                final ProcessedDetectCodeLocation processedCodeLocation = new ProcessedDetectCodeLocation(codeLocation, codeLocationName, createBdioName(codeLocationName, integrationEscapeUtil));
                processedDetectCodeLocations.add(processedCodeLocation);
            } else {
                logger.error("Created a code location name but no code locations.");
            }
        }

        //Sanity check that code location names are unique (they should be)
        Map<String, Long> result = processedDetectCodeLocations.stream().collect(Collectors.groupingBy(it -> it.codeLocationName, Collectors.counting()));
        for (final String name : result.keySet()) {
            if (result.get(name) > 1) {
                logger.error("Multiple code locations were generated with the name: " + name);

                failedBomTools.addAll(processedDetectCodeLocations.stream()
                        .filter(it -> it.codeLocationName.equals(name))
                        .map(it -> it.codeLocation.getBomToolType())
                        .collect(Collectors.toSet()));
            }
        }

        //sanity check that bdio file names are unique (they should be)
        result = processedDetectCodeLocations.stream().collect(Collectors.groupingBy(it -> it.bdioName, Collectors.counting()));
        for (final String name : result.keySet()) {
            if (result.get(name) > 1) {
                logger.error("Multiple bdio names were generated with the name: " + name);

                failedBomTools.addAll(processedDetectCodeLocations.stream()
                        .filter(it -> it.bdioName.equals(name))
                        .map(it -> it.codeLocation.getBomToolType())
                        .collect(Collectors.toSet()));
            }
        }
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

    public List<ProcessedDetectCodeLocation> getProcessedCodeLocations() {
        return processedDetectCodeLocations;
    }

    public String getCodeLocationName(final DetectCodeLocation key) {
        return processedDetectCodeLocations.stream()
                .filter(it -> it.codeLocation.equals(key))
                .map(it -> it.codeLocationName)
                .findFirst()
                .get();
    }

    public Set<BomToolType> getFailedBomTools() {
        return failedBomTools;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectVersionName() {
        return projectVersionName;
    }

    public String getCodeLocationNamePrefix() {
        return codeLocationNamePrefix;
    }

    public String getCodeLocationNameSuffix() {
        return codeLocationNameSuffix;
    }

}
