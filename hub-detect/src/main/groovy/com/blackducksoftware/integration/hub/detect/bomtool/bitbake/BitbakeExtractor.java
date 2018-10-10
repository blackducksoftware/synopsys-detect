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
package com.blackducksoftware.integration.hub.detect.bomtool.bitbake;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.type.ExecutableType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class BitbakeExtractor {
    public static final String RECIPE_DEPENDS_FILE_NAME = "recipe-depends.dot";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableManager executableManager;
    private final ExecutableRunner executableRunner;
    private final DetectConfiguration detectConfiguration;
    private final DetectFileManager detectFileManager;
    private final DetectFileFinder detectFileFinder;
    private final BitbakeOutputTransformer bitbakeOutputTransformer;

    public BitbakeExtractor(final ExecutableManager executableManager, final ExecutableRunner executableRunner, final DetectConfiguration detectConfiguration, final DetectFileManager detectFileManager,
        final DetectFileFinder detectFileFinder, final BitbakeOutputTransformer bitbakeOutputTransformer) {
        this.executableManager = executableManager;
        this.executableRunner = executableRunner;
        this.detectConfiguration = detectConfiguration;
        this.detectFileManager = detectFileManager;
        this.detectFileFinder = detectFileFinder;
        this.bitbakeOutputTransformer = bitbakeOutputTransformer;
    }

    public Extraction extract(final ExtractionId extractionId, final String foundBuildEnvScriptPath, final String sourcePath) {
        final String[] packageNames = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BITBAKE_PACKAGE_NAMES);

        final List<BitbakeOutput> bitbakeOutputs = new ArrayList<>();
        for (final String packageName : packageNames) {
            final Optional<BitbakeOutput> bitbakeOutput = executeBitbake(extractionId, foundBuildEnvScriptPath, packageName);
            bitbakeOutput.ifPresent(bitbakeOutputs::add);
        }

        final List<DetectCodeLocation> detectCodeLocations = bitbakeOutputs.stream()
                                                                 .map(bitbakeOutput -> bitbakeOutputTransformer.transformBitbakeOutput(bitbakeOutput, sourcePath))
                                                                 .filter(Optional::isPresent)
                                                                 .map(Optional::get)
                                                                 .collect(Collectors.toList());

        final Extraction extraction;

        if (detectCodeLocations.isEmpty()) {
            extraction = new Extraction.Builder()
                             .failure("No Code Locations were generated during extraction")
                             .build();
        } else {
            extraction = new Extraction.Builder()
                             .success(detectCodeLocations)
                             .build();
        }

        return extraction;
    }

    private Optional<BitbakeOutput> executeBitbake(final ExtractionId extractionId, final String foundBuildEnvScriptPath, final String packageName) {
        Optional<BitbakeOutput> bitbakeOutput = Optional.empty();

        try {
            final File outputDirectory = detectFileManager.getOutputDirectory(extractionId);
            final String bashExecutablePath = executableManager.getExecutablePathOrOverride(ExecutableType.BASH, true, "", detectConfiguration.getProperty(DetectProperty.DETECT_BASH_PATH));

            final List<String> arguments = new ArrayList<>();
            arguments.add("-c");
            arguments.add(". " + foundBuildEnvScriptPath + "; bitbake -g " + packageName);
            final Executable sourceExecutable = new Executable(outputDirectory, bashExecutablePath, arguments);

            final ExecutableOutput executableOutput = executableRunner.execute(sourceExecutable);
            final String executableDescription = sourceExecutable.getExecutableDescription();
            final File bitbakeBuildDirectory = new File(outputDirectory, "build");
            final File recipeDependsFile = detectFileFinder.findFile(bitbakeBuildDirectory, RECIPE_DEPENDS_FILE_NAME);

            bitbakeOutput = Optional.of(new BitbakeOutput(executableOutput, executableDescription, recipeDependsFile));
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed during execution of bitbake against package name [%s]", packageName));
            logger.debug(e.getMessage(), e);
        }

        return bitbakeOutput;
    }

}
