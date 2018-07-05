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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.bomtool.pip.parse.PipInspectorTreeParser;
import com.blackducksoftware.integration.hub.detect.bomtool.pip.parse.PipParseResult;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

public class PipInspectorExtractor {
    private final ExecutableRunner executableRunner;
    private final PipInspectorTreeParser pipInspectorTreeParser;
    private final DetectConfigWrapper detectConfigWrapper;

    public PipInspectorExtractor(final ExecutableRunner executableRunner, final PipInspectorTreeParser pipInspectorTreeParser, final DetectConfigWrapper detectConfigWrapper) {
        this.executableRunner = executableRunner;
        this.pipInspectorTreeParser = pipInspectorTreeParser;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public Extraction extract(final File directory, final String pythonExe, final File pipInspector, final File setupFile, final String requirementFilePath) {
        Extraction extractionResult;
        try {
            final String projectName = getProjectName(directory, pythonExe, setupFile);
            final PipParseResult result;

            final String inspectorOutput = runInspector(directory, pythonExe, pipInspector, projectName, requirementFilePath);
            result = pipInspectorTreeParser.parse(inspectorOutput, directory.toString());

            if (result == null) {
                extractionResult = new Extraction.Builder().failure("The Pip Inspector tree parser returned null").build();
            } else {
                extractionResult = new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
            }
        } catch (final Exception e) {
            extractionResult = new Extraction.Builder().exception(e).build();
        }

        return extractionResult;
    }

    private String runInspector(final File sourceDirectory, final String pythonPath, final File inspectorScript, final String projectName, final String requirementsFilePath) throws ExecutableRunnerException {
        final List<String> inspectorArguments = new ArrayList<>();
        inspectorArguments.add(inspectorScript.getAbsolutePath());

        if (StringUtils.isNotBlank(requirementsFilePath)) {
            final File requirementsFile = new File(requirementsFilePath);
            inspectorArguments.add(String.format("--requirements=%s", requirementsFile.getAbsolutePath()));
        }

        if (StringUtils.isNotBlank(projectName)) {
            inspectorArguments.add(String.format("--projectname=%s", projectName));
        }

        final Executable pipInspector = new Executable(sourceDirectory, pythonPath, inspectorArguments);
        return executableRunner.execute(pipInspector).getStandardOutput();
    }

    private String getProjectName(final File directory, final String pythonExe, final File setupFile) throws ExecutableRunnerException {
        String projectName = detectConfigWrapper.getProperty(DetectProperty.DETECT_PIP_PROJECT_NAME);

        if (setupFile != null && setupFile.exists() && StringUtils.isBlank(projectName)) {
            final Executable findProjectNameExecutable = new Executable(directory, pythonExe, Arrays.asList(
                    setupFile.getAbsolutePath(),
                    "--name"));
            final List<String> output = executableRunner.execute(findProjectNameExecutable).getStandardOutputAsList();
            projectName = output.get(output.size() - 1).replace('_', '-').trim();
        }

        return projectName;
    }

}
