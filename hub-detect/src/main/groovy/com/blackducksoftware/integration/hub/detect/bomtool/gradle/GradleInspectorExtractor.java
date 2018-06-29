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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.parse.GradleParseResult;
import com.blackducksoftware.integration.hub.detect.bomtool.gradle.parse.GradleReportParser;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

@Component
public class GradleInspectorExtractor {

    @Autowired
    public DetectConfiguration detectConfiguration;

    @Autowired
    public ExecutableRunner executableRunner;

    @Autowired
    public DetectFileFinder detectFileFinder;

    @Autowired
    public DetectFileManager detectFileManager;

    @Autowired
    GradleReportParser gradleReportParser;

    public Extraction extract(final BomToolType bomToolType, final File directory, final String gradleExe, final String gradleInspector, final ExtractionId extractionId) {
        try {
            String gradleCommand = detectConfiguration.getGradleBuildCommand();
            gradleCommand = gradleCommand.replaceAll("dependencies", "").trim();

            final List<String> arguments = new ArrayList<>();
            if (StringUtils.isNotBlank(gradleCommand)) {
                for (final String arg : gradleCommand.split(" ")) {
                    if (StringUtils.isNotBlank(arg)) {
                        arguments.add(arg);
                    }
                }
            }
            arguments.add("dependencies");
            arguments.add(String.format("--init-script=%s", gradleInspector));

            // logger.info("using ${gradleInspectorManager.getInitScriptPath()} as the path for the gradle init script");
            final Executable executable = new Executable(directory, gradleExe, arguments);
            final ExecutableOutput output = executableRunner.execute(executable);

            if (output.getReturnCode() == 0) {
                final File buildDirectory = new File(directory, "build");
                final File blackduckDirectory = new File(buildDirectory, "blackduck");

                final List<File> codeLocationFiles = detectFileFinder.findFiles(blackduckDirectory, "*_dependencyGraph.txt");

                final List<DetectCodeLocation> codeLocations = new ArrayList<>();
                String projectName = null;
                String projectVersion = null;
                if (codeLocationFiles != null) {
                    for (final File file : codeLocationFiles) {
                        final InputStream stream = new FileInputStream(file);
                        final GradleParseResult result = gradleReportParser.parseDependencies(bomToolType, stream);
                        stream.close();
                        final DetectCodeLocation codeLocation = result.codeLocation;
                        codeLocations.add(codeLocation);
                        if (projectName == null) {
                            projectName = result.projectName;
                            projectVersion = result.projectVersion;
                        }
                    }
                }
                detectFileManager.cleanupOutputFile(extractionId, blackduckDirectory);
                return new Extraction.Builder().success(codeLocations).projectName(projectName).projectVersion(projectVersion).build();
            } else {
                return new Extraction.Builder().failure("The gradle inspector returned a non-zero exit code: " + output.getReturnCode()).build();
            }

        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
