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

    public MavenCliExtractor(ExecutableRunner executableRunner, MavenCodeLocationPackager mavenCodeLocationPackager) {
        this.executableRunner = executableRunner;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(File directory, File mavenExe, MavenCliExtractorOptions mavenCliExtractorOptions) {
        try {
            String[] mavenCommand = mavenCliExtractorOptions.getMavenBuildCommand()
                                        .map(cmd -> cmd.replace("dependency:tree", ""))
                                        .map(String::trim)
                                        .map(cmd -> cmd.split(" "))
                                        .orElse(null);

            List<String> arguments = new ArrayList<>();
            if (mavenCommand != null) {
                arguments.addAll(Arrays.asList(mavenCommand));
            }
            arguments.add("dependency:tree");
            arguments.add("-T1"); // Force maven to use a single thread to ensure the tree output is in the correct order.

            ExecutableOutput mvnOutput = executableRunner.execute(directory, mavenExe, arguments);

            if (mvnOutput.getReturnCode() == 0) {
                // TODO: Improve null handling.
                List<String> excludedScopes = mavenCliExtractorOptions.getMavenExcludedScopes();
                List<String> includedScopes = mavenCliExtractorOptions.getMavenIncludedScopes();
                List<String> excludedModules = mavenCliExtractorOptions.getMavenExcludedModules();
                List<String> includedModules = mavenCliExtractorOptions.getMavenIncludedModules();
                List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(directory.toString(), mvnOutput.getStandardOutput(), excludedScopes, includedScopes, excludedModules, includedModules);

                List<CodeLocation> codeLocations = mavenResults.stream()
                                                       .map(mavenResult -> mavenResult.getCodeLocation())
                                                       .collect(Collectors.toList());

                Optional<MavenParseResult> firstWithName = mavenResults.stream()
                                                               .filter(it -> StringUtils.isNoneBlank(it.getProjectName()))
                                                               .findFirst();

                Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
                if (firstWithName.isPresent()) {
                    builder.projectName(firstWithName.get().getProjectName());
                    builder.projectVersion(firstWithName.get().getProjectVersion());
                }
                return builder.build();
            } else {
                Extraction.Builder builder = new Extraction.Builder().failure(String.format("Executing command '%s' returned a non-zero exit code %s", String.join(" ", arguments), mvnOutput.getReturnCode()));
                return builder.build();
            }
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
