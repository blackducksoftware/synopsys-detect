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
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.common.util.Bdc;
import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.executable.ExecutableResult;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.extraction.Extraction;

public class MavenCliExtractor {
    private final ExecutableRunner executableRunner;
    private final MavenCodeLocationPackager mavenCodeLocationPackager;

    public MavenCliExtractor(ExecutableRunner executableRunner, MavenCodeLocationPackager mavenCodeLocationPackager) {
        this.executableRunner = executableRunner;
        this.mavenCodeLocationPackager = mavenCodeLocationPackager;
    }

    //TODO: Limit 'extractors' to 'execute' and 'read', delegate all other work.
    public Extraction extract(File directory, File mavenExe, MavenCliExtractorOptions mavenCliExtractorOptions) {
        String[] mavenCommand = mavenCliExtractorOptions.getMavenBuildCommand()
                                    .map(cmd -> cmd.replace("dependency:tree", ""))
                                    .map(String::trim)
                                    .map(cmd -> cmd.split(" "))
                                    .orElse(null);

        List<String> arguments = Bdc.listFromArrayArgs(mavenCommand, "dependency:tree", "-T1");

        ExecutableResult mvnExecutableResult = ExecutableResult.wrap(executableRunner, directory, mavenExe, arguments);
        if (!mvnExecutableResult.hasOutputWithReturnCodeZero()) {
            return Extraction.fromFailedExecutable(mvnExecutableResult);
        }

        List<String> mavenOutput = mvnExecutableResult.getStandardOutputAsList();
        String excludedScopes = mavenCliExtractorOptions.getMavenExcludedScopes().orElse(null);
        String includedScopes = mavenCliExtractorOptions.getMavenIncludedScopes().orElse(null);
        String excludedModules = mavenCliExtractorOptions.getMavenExcludedModules().orElse(null);
        String includedModules = mavenCliExtractorOptions.getMavenIncludedModules().orElse(null);
        List<MavenParseResult> mavenResults = mavenCodeLocationPackager.extractCodeLocations(directory.toString(), mavenOutput, excludedScopes, includedScopes, excludedModules, includedModules);

        List<CodeLocation> codeLocations = Bds.of(mavenResults)
                                               .map(MavenParseResult::getCodeLocation)
                                               .toList();

        Optional<MavenParseResult> firstWithName = Bds.of(mavenResults)
                                                       .firstFiltered(it -> StringUtils.isNotBlank(it.getProjectName()));

        Extraction.Builder builder = new Extraction.Builder().success(codeLocations);
        if (firstWithName.isPresent()) {
            builder.projectName(firstWithName.get().getProjectName());
            builder.projectVersion(firstWithName.get().getProjectVersion());
        }
        return builder.build();
    }
}
