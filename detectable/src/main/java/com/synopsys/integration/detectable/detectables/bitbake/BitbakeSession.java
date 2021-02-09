/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.bitbake;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.bitbake.model.BitbakeRecipe;
import com.synopsys.integration.detectable.detectables.bitbake.parse.BitbakeRecipesParser;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class BitbakeSession {
    private static final String TASK_DEPENDS_FILE_NAME = "task-depends.dot";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final DetectableExecutableRunner executableRunner;
    private final BitbakeRecipesParser bitbakeRecipesParser;
    private final File workingDirectory;
    private final File buildEnvScript;
    private final List<String> sourceArguments;
    private final ExecutableTarget bashExecutable;

    public BitbakeSession(FileFinder fileFinder, DetectableExecutableRunner executableRunner, BitbakeRecipesParser bitbakeRecipesParser, File workingDirectory, File buildEnvScript,
        List<String> sourceArguments,
        ExecutableTarget bashExecutable) {
        this.fileFinder = fileFinder;
        this.executableRunner = executableRunner;
        this.bitbakeRecipesParser = bitbakeRecipesParser;
        this.workingDirectory = workingDirectory;
        this.buildEnvScript = buildEnvScript;
        this.sourceArguments = sourceArguments;
        this.bashExecutable = bashExecutable;
    }

    public Optional<File> executeBitbakeForDependencies(File sourceDirectory, String packageName, Integer searchDepth)
        throws ExecutableRunnerException, IOException {

        String bitbakeCommand = "bitbake -g " + packageName;
        ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        int returnCode = executableOutput.getReturnCode();

        if (returnCode != 0) {
            logger.error(String.format("Executing command '%s' returned a non-zero exit code %s", bitbakeCommand, returnCode));
            return Optional.empty();
        }

        return findTaskDependsFile(sourceDirectory, workingDirectory, searchDepth);

    }

    private Optional<File> findTaskDependsFile(File sourceDirectory, File outputDirectory, Integer searchDepth) {
        File file = fileFinder.findFile(outputDirectory, TASK_DEPENDS_FILE_NAME, searchDepth);
        if (file == null) {
            file = fileFinder.findFile(sourceDirectory, TASK_DEPENDS_FILE_NAME, searchDepth);
        }

        return Optional.ofNullable(file);

    }

    public List<BitbakeRecipe> executeBitbakeForRecipeLayerCatalog() throws ExecutableRunnerException, IOException, IntegrationException {
        final String bitbakeCommand = "bitbake-layers show-recipes";
        ExecutableOutput executableOutput = runBitbake(bitbakeCommand);
        if (executableOutput.getReturnCode() == 0) {
            return bitbakeRecipesParser.parseShowRecipes(executableOutput.getStandardOutputAsList());
        } else {
            throw new IntegrationException("Running command '%s' returned a non-zero exit code. Failed to extract bitbake recipe mapping.");
        }
    }

    private ExecutableOutput runBitbake(String bitbakeCommand) throws ExecutableRunnerException, IOException {
        StringBuilder sourceCommand = new StringBuilder("source " + buildEnvScript.getCanonicalPath());
        for (String sourceArgument : sourceArguments) {
            sourceCommand.append(" ");
            sourceCommand.append(sourceArgument);
        }
        return executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, bashExecutable, "-c", sourceCommand.toString() + "; " + bitbakeCommand));
    }
}
