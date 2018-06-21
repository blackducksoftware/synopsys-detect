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
package com.blackducksoftware.integration.hub.detect.bomtool.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.maven.parse.MavenCodeLocationPackager;
import com.blackducksoftware.integration.hub.detect.bomtool.maven.parse.MavenParseResult;
import com.blackducksoftware.integration.hub.detect.configuration.BomToolConfig;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class MavenCliExtractor {
    private final ExecutableRunner executableRunner;
    private final DetectFileFinder detectFileFinder;
    private final MavenCodeLocationPackager mavenCodeLocationPackager;
    private final BomToolConfig bomToolConfig;

    @Autowired
    public MavenCliExtractor(final ExecutableRunner executableRunner, final DetectFileFinder detectFileFinder, final MavenCodeLocationPackager mavenCodeLocationPackager, final BomToolConfig bomToolConfig) {
        this.executableRunner = executableRunner;
        this.detectFileFinder = detectFileFinder;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
        this.bomToolConfig = bomToolConfig;
    }

    public Extraction extract(final File directory, final String mavenExe) {
        try {
            String mavenCommand = bomToolConfig.getMavenBuildCommand();
            if (StringUtils.isNotBlank(mavenCommand)) {
                mavenCommand = mavenCommand.replace("dependency:tree", "");
                if (StringUtils.isNotBlank(mavenCommand)) {
                    mavenCommand = mavenCommand.trim();
                }
            }

            final List<String> arguments = new ArrayList<>();
            if (StringUtils.isNotBlank(mavenCommand)) {
                arguments.addAll(Arrays.asList(mavenCommand.split(" ")));
            }
            if (StringUtils.isNotBlank(bomToolConfig.getMavenScope())) {
                arguments.add(String.format("-Dscope=%s", bomToolConfig.getMavenScope()));
            }
            arguments.add("dependency:tree");

            final Executable mvnExecutable = new Executable(directory, mavenExe, arguments);
            final ExecutableOutput mvnOutput = executableRunner.execute(mvnExecutable);

            if (mvnOutput.getReturnCode() == 0) {

                final String excludedModules = bomToolConfig.getMavenExcludedModuleNames();
                final String includedModules = bomToolConfig.getMavenIncludedModuleNames();
                final List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(directory.toString(), mvnOutput.getStandardOutput(), excludedModules, includedModules);

                final List<DetectCodeLocation> codeLocations = mavenResults.stream()
                        .map(it -> it.codeLocation)
                        .collect(Collectors.toList());

                final Optional<MavenParseResult> firstWithName = mavenResults.stream()
                        .filter(it -> StringUtils.isNoneBlank(it.projectName))
                        .findFirst();

                final Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
                if (firstWithName.isPresent()) {
                    builder.projectName(firstWithName.get().projectName);
                    builder.projectVersion(firstWithName.get().projectVersion);
                }
                return builder.build();
            } else {
                final Extraction.Builder builder = new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", arguments), mvnOutput.getReturnCode()));
                return builder.build();
            }
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
