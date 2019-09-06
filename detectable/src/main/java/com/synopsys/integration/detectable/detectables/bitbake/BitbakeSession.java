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
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeFileType;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeResult;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeLayersParser;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.log.Slf4jIntLogger;

public class BitbakeSession {
    private final IntLogger logger = new Slf4jIntLogger(LoggerFactory.getLogger(this.getClass()));

    private final FileFinder fileFinder;
    private final ExecutableRunner executableRunner;
    private final BitbakeLayersParser bitbakeLayersParser;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final File outputDirectory;
    private final File buildEnvScript;
    private final String[] sourceArguments;
    private final File bashExecutable;

    public BitbakeSession(final FileFinder fileFinder, final ExecutableRunner executableRunner, final BitbakeLayersParser bitbakeLayersParser,
        final BitbakeRecipesParser bitbakeRecipesParser, final File outputDirectory,
        final File buildEnvScript, final String[] sourceArguments,
        final File bashExecutable) {
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.bitbakeLayersParser = bitbakeLayersParser;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.outputDirectory = outputDirectory;
        this.buildEnvScript = buildEnvScript;
        this.sourceArguments = sourceArguments;
        this.bashExecutable = bashExecutable;
    }

    public Optional<BitbakeResult> executeBitbakeForDependencies(final File sourceDirectory, final String packageName)
        throws ExecutableRunnerException, IOException {
        final String bitbakeCommand = "bitbake -g " + packageName;
        final ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        final int returnCode = executableOutput.getReturnCode();
        BitbakeResult bitbakeResult = null;

        if (returnCode == 0) {
            for (final BitbakeFileType bitbakeFileType : BitbakeFileType.values()) {
                File file = fileFinder.findFiles(outputDirectory, bitbakeFileType.getFileName(), 1).stream().findFirst().orElse(null);

                if (file != null) {
                    bitbakeResult = new BitbakeResult(bitbakeFileType, file);
                    break;
                } else {
                    // If we didn't find the files where we expect, also look in the sourceDirectory. See IDETECT-1493.
                    file = fileFinder.findFiles(sourceDirectory, bitbakeFileType.getFileName(), 1).stream().findFirst().orElse(null);
                    if (file != null) {
                        bitbakeResult = new BitbakeResult(bitbakeFileType, file);
                        break;
                    }
                }
            }
        } else {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
        }

        return Optional.ofNullable(bitbakeResult);
    }

    public Map<String, List<String>> executeBitbakeForRecipeMap() throws ExecutableRunnerException, IOException, IntegrationException {
        final String bitbakeCommand = "bitbake-layers show-recipes";
        final ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        if (executableOutput.getReturnCode() == 0) {
            return bitbakeRecipesParser.parseComponentLayerMap(executableOutput.getStandardOutput());
        } else {
            throw new IntegrationException("Running command '%s' returned a non-zero exit code. Failed to extract bitbake recipe mapping.");
        }
    }

    public Map<String, Integer> executeBitbakeForLayers() throws ExecutableRunnerException, IOException, IntegrationException {
        final String bitbakeCommand = "bitbake-layers show-layers";
        final ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        if (executableOutput.getReturnCode() == 0) {
            return bitbakeLayersParser.parseLayerPriorityMap(executableOutput.getStandardOutput());
        } else {
            throw new IntegrationException("Running command '%s' returned a non-zero exit code. Failed to extract bitbake layers.");
        }
    }

    private ExecutableOutput runBitbake(final String bitbakeCommand) throws ExecutableRunnerException, IOException {
        try {
            final StringBuilder sourceCommand = new StringBuilder("source " + buildEnvScript.getCanonicalPath());
            for (final String sourceArgument : sourceArguments) {
                sourceCommand.append(" ");
                sourceCommand.append(sourceArgument);
            }
            return executableRunner.execute(outputDirectory, bashExecutable, "-c", sourceCommand.toString() + "; " + bitbakeCommand);
        } catch (final ExecutableRunnerException e) {
            logger.error(String.format("Failed executing bitbake command. %s", bitbakeCommand));
            logger.debug(e.getMessage(), e);
            throw e;
        }
    }
}
