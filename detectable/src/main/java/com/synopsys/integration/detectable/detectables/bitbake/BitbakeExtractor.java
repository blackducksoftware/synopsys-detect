/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.bitbake;

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

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocationType;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeArchitectureParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.exception.IntegrationException;

public class BitbakeExtractor {
    public static final String RECIPE_DEPENDS_FILE_NAME = "recipe-depends.dot";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final FileFinder fileFinder;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeArchitectureParser bitbakeArchitectureParser;

    public BitbakeExtractor(final ExecutableRunner executableRunner,
        final FileFinder fileFinder, final GraphParserTransformer graphParserTransformer, final BitbakeGraphTransformer bitbakeGraphTransformer,
        final BitbakeArchitectureParser bitbakeArchitectureParser) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeArchitectureParser = bitbakeArchitectureParser;
    }

    public Extraction extract(final ExtractionEnvironment extractionEnvironment, final File buildEnvScript, final File sourcePath, String[] packageNames, File bash) {
        final File outputDirectory = extractionEnvironment.getOutputDirectory();
        final File bitbakeBuildDirectory = new File(outputDirectory, "build");

        final List<CodeLocation> codeLocations = new ArrayList<>();
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
                final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser);
                final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, targetArchitecture);

                final ExternalId externalId = new ExternalId(Forge.YOCTO);
                final CodeLocation codeLocation = new CodeLocation.Builder(CodeLocationType.BITBAKE, sourcePath.getCanonicalPath(), externalId, dependencyGraph).build();

                codeLocations.add(codeLocation);
            } catch (final IOException | IntegrationException e) {
                logger.error(String.format("Failed to extract a Code Location while running Bitbake against package '%s'", packageName));
                logger.debug(e.getMessage(), e);
            }
        }

        final Extraction extraction;

        if (codeLocations.isEmpty()) {
            extraction = new Extraction.Builder()
                             .failure("No Code Locations were generated during extraction")
                             .build();
        } else {
            extraction = new Extraction.Builder()
                             .success(codeLocations)
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
            recipeDependsFile = fileFinder.findFile(bitbakeBuildDirectory, RECIPE_DEPENDS_FILE_NAME);
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
            targetArchitecture = bitbakeArchitectureParser.architectureFromOutput(executableOutput.getStandardOutput()).orElse(null);
        } else {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return targetArchitecture;
    }

    private ExecutableOutput runBitbake(final File outputDirectory, final File buildEnvScript, final String bitbakeCommand, File bash) {
        try {
            return executableRunner.execute(outputDirectory, bash, "-c", ". " + buildEnvScript + "; " + bitbakeCommand);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed executing bitbake command."));
            logger.debug(e.getMessage(), e);
        }

        return null;
    }
}
