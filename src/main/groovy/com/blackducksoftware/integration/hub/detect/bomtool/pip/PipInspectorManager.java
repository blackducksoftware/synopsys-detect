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
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunnerException;

@Component
public class PipInspectorManager {
    public static final String INSPECTOR_NAME = "pip-inspector.py";
    final Logger logger = LoggerFactory.getLogger(PipInspectorManager.class);
    @Autowired
    private ExecutableRunner executableRunner;

    @Autowired
    private DetectFileManager detectFileManager;

    public File extractInspectorScript() throws IOException {
        final InputStream insptectorFileStream = getClass().getResourceAsStream(String.format("/%s", INSPECTOR_NAME));
        final String inpsectorScriptContents = IOUtils.toString(insptectorFileStream, StandardCharsets.UTF_8);
        final File inspectorScript = detectFileManager.createSharedFile("pip", INSPECTOR_NAME);
        return detectFileManager.writeToFile(inspectorScript, inpsectorScriptContents);
    }

    public String runInspector(final File sourceDirectory, final String pythonPath, final File inspectorScript, final String projectName, final String requirementsFilePath) throws ExecutableRunnerException {
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

}
