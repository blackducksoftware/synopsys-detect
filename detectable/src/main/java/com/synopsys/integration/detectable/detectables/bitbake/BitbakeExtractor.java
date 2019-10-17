/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.paypal.digraph.parser.GraphParser;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeFileType;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeGraph;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeResult;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeArchitectureParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeGraphTransformer;
import com.synopsys.integration.detectable.detectables.bitbake.parse.GraphParserTransformer;
import com.synopsys.integration.exception.IntegrationException;

public class BitbakeExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final FileFinder fileFinder;
    private final GraphParserTransformer graphParserTransformer;
    private final BitbakeGraphTransformer bitbakeGraphTransformer;
    private final BitbakeArchitectureParser bitbakeArchitectureParser;

    public BitbakeExtractor(final ExecutableRunner executableRunner, final FileFinder fileFinder, final GraphParserTransformer graphParserTransformer, final BitbakeGraphTransformer bitbakeGraphTransformer,
        final BitbakeArchitectureParser bitbakeArchitectureParser) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.graphParserTransformer = graphParserTransformer;
        this.bitbakeGraphTransformer = bitbakeGraphTransformer;
        this.bitbakeArchitectureParser = bitbakeArchitectureParser;
    }

    public Extraction extract(final File workingDirectory, final File buildEnvScript, final String[] packageNames, final File bash, final String referenceImplementation) {
        final List<CodeLocation> codeLocations = new ArrayList<>();
        for (final String packageName : packageNames) {
            try {
                final Optional<BitbakeResult> bitbakeResult = executeBitbakeForDependencies(workingDirectory, buildEnvScript, packageName, bash);
                if (!bitbakeResult.isPresent()) {
                    final String filesSearchedFor = StringUtils.joinWith(", ", Arrays.stream(BitbakeFileType.values()).map(BitbakeFileType::getFileName).collect(Collectors.toList()));
                    throw new IntegrationException(String.format("Failed to find any bitbake results. Looked for: %s", filesSearchedFor));
                }

                final String targetArchitecture = executeBitbakeForTargetArchitecture(workingDirectory, buildEnvScript, packageName, bash)
                                                      .map(architecture -> architecture.replace(referenceImplementation, ""))
                                                      .map(StringUtils::stripToNull)
                                                      .orElseThrow(() -> new IntegrationException("Failed to find a target architecture"));

                final File fileToParse = bitbakeResult.get().getFile();
                logger.trace(FileUtils.readFileToString(fileToParse, Charset.defaultCharset()));
                final InputStream dependsFileInputStream = FileUtils.openInputStream(fileToParse);
                final GraphParser graphParser = new GraphParser(dependsFileInputStream);

                final BitbakeFileType bitbakeFileType = bitbakeResult.get().getBitbakeFileType();
                final BitbakeGraph bitbakeGraph = graphParserTransformer.transform(graphParser, bitbakeFileType);

                final DependencyGraph dependencyGraph = bitbakeGraphTransformer.transform(bitbakeGraph, targetArchitecture);
                final CodeLocation codeLocation = new CodeLocation(dependencyGraph);

                codeLocations.add(codeLocation);
            } catch (final IOException | IntegrationException | ExecutableRunnerException | NotImplementedException e) {
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

    private Optional<BitbakeResult> executeBitbakeForDependencies(final File workingDirectory, final File buildEnvScript, final String packageName, final File bash) throws ExecutableRunnerException, IOException {
        final String bitbakeCommand = "bitbake -g " + packageName;
        final ExecutableOutput executableOutput = runBitbake(workingDirectory, buildEnvScript, bitbakeCommand, bash);
        final int returnCode = executableOutput.getReturnCode();
        BitbakeResult bitbakeResult = null;

        if (returnCode == 0) {
            for (final BitbakeFileType bitbakeFileType : BitbakeFileType.values()) {
                final File file = fileFinder.findFiles(workingDirectory, bitbakeFileType.getFileName(), 5).stream().findFirst().orElse(null);

                if (file != null) {
                    bitbakeResult = new BitbakeResult(bitbakeFileType, file);
                    break;
                }
            }
        } else {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return Optional.ofNullable(bitbakeResult);
    }

    private Optional<String> executeBitbakeForTargetArchitecture(final File workingDirectory, final File buildEnvScript, final String packageName, final File bash) throws ExecutableRunnerException, IOException {
        final String bitbakeCommand = "bitbake -c listtasks " + packageName;
        final ExecutableOutput executableOutput = runBitbake(workingDirectory, buildEnvScript, bitbakeCommand, bash);
        final int returnCode = executableOutput.getReturnCode();
        String targetArchitecture = null;

        if (returnCode == 0) {
            targetArchitecture = bitbakeArchitectureParser.architectureFromOutput(executableOutput.getStandardOutput()).orElse(null);
        } else {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return Optional.ofNullable(targetArchitecture);
    }

    private ExecutableOutput runBitbake(final File workingDirectory, final File buildEnvScript, final String bitbakeCommand, final File bash) throws ExecutableRunnerException, IOException {
        try {
            return executableRunner.execute(workingDirectory, bash, "-c", "source " + buildEnvScript.getCanonicalPath() + "; " + bitbakeCommand);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed executing bitbake command. %s", bitbakeCommand));
            logger.debug(e.getMessage(), e);
            throw e;
        }
    }
}
