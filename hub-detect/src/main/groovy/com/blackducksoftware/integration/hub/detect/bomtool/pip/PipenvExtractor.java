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
package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.parse.PipParseResult;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.parse.PipenvGraphParser;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class PipenvExtractor {
    public static final String PIP_SEPARATOR = "==";

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private ExecutableRunner executableRunner;

    @Autowired
    private PipenvGraphParser pipenvTreeParser;

    public Extraction extract(final File directory, final String pythonExe, final String pipenvExe, final File pipfileDotLock, final File pipfile, final File setupFile) {
        Extraction extraction;

        try {
            final String projectName = getProjectName(directory, pythonExe, pipenvExe, pipfileDotLock, pipfile, setupFile);
            final String projectVersionName = getProjectVersionName(directory, pythonExe, pipenvExe, pipfileDotLock, pipfile, setupFile);
            final PipParseResult result;

            final Executable pipenvRunPipFreeze = new Executable(directory, pipenvExe, Arrays.asList("run", "pip", "freeze"));
            final ExecutableOutput pipFreezeOutput = executableRunner.execute(pipenvRunPipFreeze);

            final Executable pipenvGraph = new Executable(directory, pipenvExe, Arrays.asList("graph", "--bare"));
            final ExecutableOutput graphOutput = executableRunner.execute(pipenvGraph);

            result = pipenvTreeParser.parse(projectName, projectVersionName, pipFreezeOutput.getStandardOutputAsList(), graphOutput.getStandardOutputAsList(), directory.toString());

            if (result != null) {
                extraction = new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
            } else {
                extraction = new Extraction.Builder().failure("Pipenv graph could not successfully be parsed").build();
            }
        } catch (final Exception e) {
            extraction = new Extraction.Builder().exception(e).build();
        }

        return extraction;
    }

    private String getProjectName(final File directory, final String pythonExe, final String pipenvExe, final File pipfileDotLock, final File pipfile, final File setupFile) throws ExecutableRunnerException {
        String projectName = detectConfiguration.getPipProjectName();

        if (StringUtils.isBlank(projectName) && setupFile != null && setupFile.exists()) {
            final Executable findProjectNameExecutable = new Executable(directory, pythonExe, Arrays.asList(
                    setupFile.getAbsolutePath(),
                    "--name"));
            final List<String> output = executableRunner.execute(findProjectNameExecutable).getStandardOutputAsList();
            projectName = output.get(output.size() - 1).replace('_', '-').trim();
        }

        return projectName;
    }

    private String getProjectVersionName(final File directory, final String pythonExe, final String pipenvExe, final File pipfileDotLock, final File pipfile, final File setupFile) throws ExecutableRunnerException {
        String projectVersionName = detectConfiguration.getPipProjectVersionName();

        if (StringUtils.isBlank(projectVersionName) && setupFile != null && setupFile.exists()) {
            final Executable findProjectNameExecutable = new Executable(directory, pythonExe, Arrays.asList(
                    setupFile.getAbsolutePath(),
                    "--version"));
            final List<String> output = executableRunner.execute(findProjectNameExecutable).getStandardOutputAsList();
            projectVersionName = output.get(output.size() - 1).trim();
        }

        return projectVersionName;
    }

}
