/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.pip;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvResult;
import com.synopsys.integration.detectable.detectables.pip.parser.PipEnvJsonGraphParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvFreezeParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class PipenvExtractor {
    private final DetectableExecutableRunner executableRunner;
    private final PipenvTransformer pipenvTransformer;
    private final PipenvFreezeParser pipenvFreezeParser;
    private final PipEnvJsonGraphParser pipEnvJsonGraphParser;

    public PipenvExtractor(final DetectableExecutableRunner executableRunner, final PipenvTransformer pipenvTransformer, final PipenvFreezeParser pipenvFreezeParser, final PipEnvJsonGraphParser pipEnvJsonGraphParser) {
        this.executableRunner = executableRunner;
        this.pipenvTransformer = pipenvTransformer;
        this.pipenvFreezeParser = pipenvFreezeParser;
        this.pipEnvJsonGraphParser = pipEnvJsonGraphParser;
    }

    public Extraction extract(final File directory, final File pythonExe, final File pipenvExe, final File setupFile, final String providedProjectName, final String providedProjectVersionName, final boolean includeOnlyProjectTree) {
        final Extraction extraction;

        try {
            final String projectName = resolveProjectName(directory, pythonExe, setupFile, providedProjectName);
            final String projectVersionName = resolveProjectVersionName(directory, pythonExe, setupFile, providedProjectVersionName);

            final ExecutableOutput pipFreezeOutput = executableRunner.execute(directory, pipenvExe, Arrays.asList("run", "pip", "freeze"));
            final ExecutableOutput graphOutput = executableRunner.execute(directory, pipenvExe, Arrays.asList("graph", "--bare", "--json-tree"));

            final PipFreeze pipFreeze = pipenvFreezeParser.parse(pipFreezeOutput.getStandardOutputAsList());
            final PipenvGraph pipenvGraph = pipEnvJsonGraphParser.parse(graphOutput.getStandardOutput());
            final PipenvResult result = pipenvTransformer.transform(projectName, projectVersionName, pipFreeze, pipenvGraph, includeOnlyProjectTree);

            return new Extraction.Builder().success(result.getCodeLocation()).projectName(result.getProjectName()).projectVersion(result.getProjectVersion()).build();
        } catch (final Exception e) {
            extraction = new Extraction.Builder().exception(e).build();
        }

        return extraction;
    }

    private String resolveProjectName(final File directory, final File pythonExe, final File setupFile, final String providedProjectName) throws ExecutableRunnerException {
        String projectName = providedProjectName;

        if (StringUtils.isBlank(projectName) && setupFile != null && setupFile.exists()) {
            final List<String> arguments = Arrays.asList(setupFile.getAbsolutePath(), "--name");
            final List<String> output = executableRunner.execute(directory, pythonExe, arguments).getStandardOutputAsList();
            projectName = output.get(output.size() - 1).replace('_', '-').trim();
        }

        return projectName;
    }

    private String resolveProjectVersionName(final File directory, final File pythonExe, final File setupFile, final String providedProjectVersionName) throws ExecutableRunnerException {
        String projectVersionName = providedProjectVersionName;

        if (StringUtils.isBlank(projectVersionName) && setupFile != null && setupFile.exists()) {
            final List<String> arguments = Arrays.asList(setupFile.getAbsolutePath(), "--version");
            final List<String> output = executableRunner.execute(directory, pythonExe, arguments).getStandardOutputAsList();
            projectVersionName = output.get(output.size() - 1).trim();
        }

        return projectVersionName;
    }

}
