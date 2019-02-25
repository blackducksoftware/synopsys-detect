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
package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportTransformer;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleRootMetadataParser;
import com.synopsys.integration.util.NameVersion;

public class GradleInspectorExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExecutableRunner executableRunner;
    private final FileFinder fileFinder;
    private final GradleReportParser gradleReportParser;
    private final GradleReportTransformer gradleReportTransformer;
    private final GradleRootMetadataParser gradleRootMetadataParser;

    public GradleInspectorExtractor(final ExecutableRunner executableRunner, final FileFinder fileFinder, final GradleReportParser gradleReportParser, final GradleReportTransformer gradleReportTransformer,
        final GradleRootMetadataParser gradleRootMetadataParser) {
        this.executableRunner = executableRunner;
        this.fileFinder = fileFinder;
        this.gradleReportParser = gradleReportParser;
        this.gradleReportTransformer = gradleReportTransformer;
        this.gradleRootMetadataParser = gradleRootMetadataParser;
    }

    public Extraction extract(final File directory, final File gradleExe, String gradleCommand, final File gradleInspector, final File outputDirectory) {
        try {

            final List<String> arguments = new ArrayList<>();
            if (StringUtils.isNotBlank(gradleCommand)) {
                gradleCommand = gradleCommand.replaceAll("dependencies", "").trim();
                Arrays.stream(gradleCommand.split(" ")).filter(StringUtils::isNotBlank).forEach(arguments::add);
            }
            arguments.add("dependencies");
            arguments.add(String.format("--init-script=%s", gradleInspector));
            arguments.add(String.format("-DGRADLEEXTRACTIONDIR=%s", outputDirectory.getCanonicalPath()));
            arguments.add("--info");

            final ExecutableOutput output = executableRunner.execute(directory, gradleExe, arguments);

            if (output.getReturnCode() == 0) {
                final File rootProjectMetadataFile = fileFinder.findFile(outputDirectory, "rootProjectMetadata.txt");
                final List<File> reportFiles = fileFinder.findFiles(outputDirectory, "*_dependencyGraph.txt");

                final List<CodeLocation> codeLocations = new ArrayList<>();
                String projectName = null;
                String projectVersion = null;
                if (reportFiles != null) {
                    reportFiles.stream()
                        .map(gradleReportParser::parseReport)
                        .map(gradleReportTransformer::trasnform)
                        .forEach(codeLocations::add);

                    if (rootProjectMetadataFile != null) {
                        final Optional<NameVersion> projectNameVersion = gradleRootMetadataParser.parseRootProjectNameVersion(rootProjectMetadataFile);
                        if (projectNameVersion.isPresent()) {
                            projectName = projectNameVersion.get().getName();
                            projectVersion = projectNameVersion.get().getVersion();
                        }
                    } else {
                        logger.warn("Gradle inspector did not create a meta data report so no project version information was found.");
                    }
                }
                return new Extraction.Builder().success(codeLocations).projectName(projectName).projectVersion(projectVersion).build();
            } else {
                return new Extraction.Builder().failure("The gradle inspector returned a non-zero exit code: " + output.getReturnCode()).build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
