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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.inspector;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.NugetParseResult;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.synopsys.integration.hub.bdio.graph.DependencyGraphCombiner;
import com.synopsys.integration.hub.bdio.graph.MutableDependencyGraph;

public class NugetInspectorExtractor {
    public static final String INSPECTOR_OUTPUT_PATTERN = "*_inspection.json";

    private final Logger logger = LoggerFactory.getLogger(NugetInspectorExtractor.class);

    private final DetectFileManager detectFileManager;
    private final NugetInspectorPackager nugetInspectorPackager;
    private final ExecutableRunner executableRunner;
    private final DetectFileFinder detectFileFinder;
    private final DetectConfiguration detectConfiguration;

    public NugetInspectorExtractor(final DetectFileManager detectFileManager, final NugetInspectorPackager nugetInspectorPackager, final ExecutableRunner executableRunner, final DetectFileFinder detectFileFinder,
        final DetectConfiguration detectConfiguration) {
        this.detectFileManager = detectFileManager;
        this.nugetInspectorPackager = nugetInspectorPackager;
        this.executableRunner = executableRunner;
        this.detectFileFinder = detectFileFinder;
        this.detectConfiguration = detectConfiguration;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory, final NugetInspector nugetInspector, final ExtractionId extractionId) {
        try {
            final File outputDirectory = detectFileManager.getOutputDirectory(extractionId);

            NugetInspectorArgumentBuilder inspectorOptionsBuilder = new NugetInspectorArgumentBuilder();

            inspectorOptionsBuilder.setTargetPath(directory.toString())
                .setOutputDirectory(outputDirectory.getCanonicalPath())
                .setIgnoreFailure(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NUGET_IGNORE_FAILURE));

            final String nugetExcludedModules = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_EXCLUDED_MODULES);
            if (StringUtils.isNotBlank(nugetExcludedModules)) {
                inspectorOptionsBuilder.setExcludedModules(nugetExcludedModules);
            }
            final String nugetIncludedModules = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INCLUDED_MODULES);
            if (StringUtils.isNotBlank(nugetIncludedModules)) {
                inspectorOptionsBuilder.setIncludedModules(nugetIncludedModules);
            }
            final String[] nugetPackagesRepo = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_NUGET_PACKAGES_REPO_URL);
            if (nugetPackagesRepo.length > 0) {
                final String packagesRepos = Arrays.asList(nugetPackagesRepo).stream().collect(Collectors.joining(","));
                inspectorOptionsBuilder.setPackagesRepoUrl(packagesRepos);
            }
            final String nugetConfigPath = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_CONFIG_PATH);
            if (StringUtils.isNotBlank(nugetConfigPath)) {
                inspectorOptionsBuilder.setNugetConfigPath(nugetIncludedModules);
            }
            if (logger.isTraceEnabled()) {
                inspectorOptionsBuilder.setVerbose(true);
            }

            List<String> options = inspectorOptionsBuilder.build();
            final ExecutableOutput executableOutput = nugetInspector.execute(executableRunner, directory, options);

            if (executableOutput.getReturnCode() != 0) {
                return new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", options), executableOutput.getReturnCode())).build();
            }

            final List<File> dependencyNodeFiles = detectFileFinder.findFiles(outputDirectory, INSPECTOR_OUTPUT_PATTERN);

            final List<NugetParseResult> parseResults = new ArrayList<>();
            for (final File dependencyNodeFile : dependencyNodeFiles) {
                detectFileManager.registerFileOfInterest(extractionId, dependencyNodeFile);
                final NugetParseResult result = nugetInspectorPackager.createDetectCodeLocation(bomToolType, dependencyNodeFile);
                parseResults.add(result);
            }

            final List<DetectCodeLocation> codeLocations = parseResults.stream()
                                                               .flatMap(it -> it.codeLocations.stream())
                                                               .collect(Collectors.toList());

            if (codeLocations.size() <= 0) {
                logger.warn("Unable to extract any dependencies from nuget");
            }

            final Map<String, DetectCodeLocation> codeLocationsBySource = new HashMap<>();
            final DependencyGraphCombiner combiner = new DependencyGraphCombiner();

            codeLocations.stream().forEach(codeLocation -> {
                final String sourcePathKey = codeLocation.getSourcePath().toLowerCase();
                if (codeLocationsBySource.containsKey(sourcePathKey)) {
                    logger.info("Multiple project code locations were generated for: " + directory.toString());
                    logger.info("This most likely means the same project exists in multiple solutions.");
                    logger.info("The code location's dependencies will be combined, in the future they will exist seperately for each solution.");
                    final DetectCodeLocation destination = codeLocationsBySource.get(sourcePathKey);
                    combiner.addGraphAsChildrenToRoot((MutableDependencyGraph) destination.getDependencyGraph(), codeLocation.getDependencyGraph());
                } else {
                    codeLocationsBySource.put(sourcePathKey, codeLocation);
                }
            });

            final List<DetectCodeLocation> uniqueCodeLocations = codeLocationsBySource.values().stream().collect(Collectors.toList());

            final Extraction.Builder builder = new Extraction.Builder().success(uniqueCodeLocations);
            final Optional<NugetParseResult> project = parseResults.stream().filter(it -> StringUtils.isNotBlank(it.projectName)).findFirst();
            if (project.isPresent()) {
                builder.projectName(project.get().projectName);
                builder.projectVersion(project.get().projectVersion);
            }
            return builder.build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
