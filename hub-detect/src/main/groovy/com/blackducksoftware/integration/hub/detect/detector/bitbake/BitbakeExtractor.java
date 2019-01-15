/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.detector.bitbake;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.exception.IntegrationException;

public class BitbakeExtractor {
    public static final String RECIPE_DEPENDS_FILE_NAME = "recipe-depends.dot";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final DetectFileFinder detectFileFinder;
    private final DirectoryManager directoryManager;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeListTasksParser bitbakeListTasksParser;

    public BitbakeExtractor(final ExecutableRunner executableRunner, final DirectoryManager directoryManager,
        final DetectFileFinder detectFileFinder, final GraphParserTransformer graphParserTransformer, final BitbakeListTasksParser bitbakeListTasksParser) {
        this.executableRunner = executableRunner;
        this.directoryManager = directoryManager;
        this.detectFileFinder = detectFileFinder;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeListTasksParser = bitbakeListTasksParser;
    }

    public Extraction extract(final ExtractionId extractionId, final File buildEnvScript, final File sourcePath, String[] packageNames, File bash) {
        final File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        final File bitbakeBuildDirectory = new File(outputDirectory, "build");

        final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>();
        for (final String packageName : packageNames) {
            final File dependsFile = executeBitbakeForRecipeDependsFile(outputDirectory, bitbakeBuildDirectory, buildEnvScript, packageName, bash);
            final String targetArchitecture = executeBitbakeForTargetArchitecture(outputDirectory, buildEnvScript, packageName, bash);

            try {
                if (dependsFile == null) {
                    throw new IntegrationException(
                        String.format("Failed to find %s. This may be due to this project being a version of The Yocto Project earlier than 2.3 (Pyro) which is the minimum version for Detect", RECIPE_DEPENDS_FILE_NAME));
                }
                if (StringUtils.isBlank(targetArchitecture)) {
                    throw new IntegrationException("Failed to find a target architecture");
                }

                logger.debug(FileUtils.readFileToString(dependsFile, Charset.defaultCharset()));
                final InputStream recipeDependsInputStream = FileUtils.openInputStream(dependsFile);
                final GraphParser graphParser = new GraphParser(recipeDependsInputStream);
                final DependencyGraph dependencyGraph = graphParserTransformer.transform(graphParser, targetArchitecture);
                final ExternalId externalId = new ExternalId(Forge.YOCTO);
                final DetectCodeLocation detectCodeLocation = new DetectCodeLocation.Builder(DetectCodeLocationType.BITBAKE, sourcePath.getCanonicalPath(), externalId, dependencyGraph).build();

                detectCodeLocations.add(detectCodeLocation);
            } catch (final IOException | IntegrationException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s'", packageName));
                logger.debug(e.getMessage(), e);
            }
        }

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

    private File executeBitbakeForRecipeDependsFile(final File outputDirectory, final File bitbakeBuildDirectory, final File buildEnvScript, final String packageName, File bash) {
        final String bitbakeCommand = "bitbake -g " + packageName;
        final ExecutableOutput executableOutput = runBitbake(outputDirectory, buildEnvScript, bitbakeCommand, bash);
        final int returnCode = executableOutput.getReturnCode();
        File recipeDependsFile = null;

        if (returnCode == 0) {
            recipeDependsFile = detectFileFinder.findFile(bitbakeBuildDirectory, RECIPE_DEPENDS_FILE_NAME);
        } else {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return recipeDependsFile;
    }

    private String executeBitbakeForTargetArchitecture(final File outputDirectory, final File buildEnvScript, final String packageName, File bash) {
        final String bitbakeCommand = "bitbake -c listtasks " + packageName;
        final ExecutableOutput executableOutput = runBitbake(outputDirectory, buildEnvScript, bitbakeCommand, bash);
        final int returnCode = executableOutput.getReturnCode();
        String targetArchitecture = null;

        if (returnCode == 0) {
            targetArchitecture = bitbakeListTasksParser.parseTargetArchitecture(executableOutput.getStandardOutput()).orElse(null);
        } else {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return targetArchitecture;
    }

    private ExecutableOutput runBitbake(final File outputDirectory, final File buildEnvScript, final String bitbakeCommand, File bash) {

        final List<String> arguments = new ArrayList<>();
        arguments.add("-c");
        arguments.add(". " + buildEnvScript + "; " + bitbakeCommand);
        final Executable sourceExecutable = new Executable(outputDirectory, bash, arguments);
        ExecutableOutput executableOutput = null;

        try {
            executableOutput = executableRunner.execute(sourceExecutable);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed executing command '%s'", sourceExecutable.getExecutableDescription()));
            logger.debug(e.getMessage(), e);
        }

        return executableOutput;
    }
}
