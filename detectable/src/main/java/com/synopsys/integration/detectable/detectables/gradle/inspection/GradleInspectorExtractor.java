/*
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
package com.synopsys.integration.detectable.detectables.gradle.inspection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportParser;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleReportTransformer;
import com.synopsys.integration.detectable.detectables.gradle.inspection.parse.GradleRootMetadataParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.util.NameVersion;

public class GradleInspectorExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final FileFinder fileFinder;
    private final GradleRunner gradleRunner;
    private final GradleReportParser gradleReportParser;
    private final GradleReportTransformer gradleReportTransformer;
    private final GradleRootMetadataParser gradleRootMetadataParser;

    public GradleInspectorExtractor(FileFinder fileFinder, GradleRunner gradleRunner, GradleReportParser gradleReportParser,
        GradleReportTransformer gradleReportTransformer,
        GradleRootMetadataParser gradleRootMetadataParser) {
        this.fileFinder = fileFinder;
        this.gradleRunner = gradleRunner;
        this.gradleReportParser = gradleReportParser;
        this.gradleReportTransformer = gradleReportTransformer;
        this.gradleRootMetadataParser = gradleRootMetadataParser;
    }

    public Extraction extract(File directory, ExecutableTarget gradleExe, @Nullable String gradleCommand, ProxyInfo proxyInfo, File gradleInspector, File outputDirectory) throws ExecutableFailedException {
        try {
            gradleRunner.runGradleDependencies(directory, gradleExe, gradleInspector, gradleCommand, proxyInfo, outputDirectory);

            File rootProjectMetadataFile = fileFinder.findFile(outputDirectory, "rootProjectMetadata.txt");
            List<File> reportFiles = fileFinder.findFiles(outputDirectory, "*_dependencyGraph.txt");

            List<CodeLocation> codeLocations = new ArrayList<>();
            String projectName = null;
            String projectVersion = null;
            if (reportFiles != null) {
                reportFiles.stream()
                    .map(gradleReportParser::parseReport)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(gradleReportTransformer::transform)
                    .forEach(codeLocations::add);

                if (rootProjectMetadataFile != null) {
                    Optional<NameVersion> projectNameVersion = gradleRootMetadataParser.parseRootProjectNameVersion(rootProjectMetadataFile);
                    if (projectNameVersion.isPresent()) {
                        projectName = projectNameVersion.get().getName();
                        projectVersion = projectNameVersion.get().getVersion();
                    }
                } else {
                    logger.warn("Gradle inspector did not create a meta data report so no project version information was found.");
                }
            }

            return new Extraction.Builder()
                       .success(codeLocations)
                       .projectName(projectName)
                       .projectVersion(projectVersion)
                       .build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
