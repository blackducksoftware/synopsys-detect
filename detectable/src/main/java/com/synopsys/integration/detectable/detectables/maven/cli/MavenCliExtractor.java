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
package com.synopsys.integration.detectable.detectables.maven.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;

public class MavenCliExtractor {
    private final ExecutableRunner executableRunner;
    private final MavenCodeLocationPackager mavenCodeLocationPackager;
    private final MavenCliExtractorOptions mavenCliExtractorOptions;

    public MavenCliExtractor(final ExecutableRunner executableRunner, final MavenCodeLocationPackager mavenCodeLocationPackager, final MavenCliExtractorOptions mavenCliExtractorOptions) {
        this.executableRunner = executableRunner;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
        this.mavenCliExtractorOptions = mavenCliExtractorOptions;
    }

    public Extraction extract(final File directory, final File mavenExe) {
        try {
            String mavenCommand = mavenCliExtractorOptions.getMavenBuildCommand();
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
            arguments.add("dependency:tree");

            final ExecutableOutput mvnOutput = executableRunner.execute(directory, mavenExe, arguments);

            if (mvnOutput.getReturnCode() == 0) {

                final String mavenScope = mavenCliExtractorOptions.getMavenScope();
                final String excludedModules = mavenCliExtractorOptions.getMavenExcludedModules();
                final String includedModules = mavenCliExtractorOptions.getMavenIncludedModules();
                final List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(directory.toString(), mvnOutput.getStandardOutput(),
                    mavenScope, excludedModules, includedModules);

                final List<CodeLocation> codeLocations = mavenResults.stream()
                                                             .map(mavenResult -> mavenResult.codeLocation)
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
