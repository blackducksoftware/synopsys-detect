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
package com.synopsys.integration.detectable.detectables.conan.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.conan.ConanDetectableResult;
import com.synopsys.integration.detectable.detectables.conan.cli.parser.ConanInfoParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.executable.ExecutableOutput;

public class ConanCliExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final ConanInfoParser conanInfoParser;

    public ConanCliExtractor(DetectableExecutableRunner executableRunner, ConanInfoParser conanInfoParser) {
        this.executableRunner = executableRunner;
        this.conanInfoParser = conanInfoParser;
    }

    public Extraction extract(File projectDir, File conanExe, ConanCliExtractorOptions conanCliExtractorOptions) {
        // TODO refactor this out
        List<String> exeArgs = new ArrayList<>();
        exeArgs.add("info");
        if (conanCliExtractorOptions.getAdditionalArguments().isPresent()) {
            String[] additionalArgs = conanCliExtractorOptions.getAdditionalArguments().get().split(" +");
            for (String additionalArg : additionalArgs) {
                exeArgs.add(additionalArg);
            }
        }
        // TODO this should be the recipe file?
        exeArgs.add(projectDir.getAbsolutePath()); // TODO What if conanfile is in a subdir?

        ExecutableOutput conanInfoOutput;
        try {
            conanInfoOutput = executableRunner.execute(projectDir, conanExe, exeArgs);
        } catch (Exception e) {
            logger.error(String.format("Exception thrown executing conan info command: %s", e.getMessage()));
            return new Extraction.Builder().exception(e).build();
        }
        String standardOutput = conanInfoOutput.getStandardOutput();
        String errorOutput = conanInfoOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput) && errorOutput.contains("ERROR: ")) {
            logger.error(String.format("The conan info command reported errors: %s", errorOutput));
            return new Extraction.Builder().failure("Conan info command reported errors").build();
        }
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.debug(String.format("The conan info command wrote to stderr: %s", errorOutput));
        }
        if (StringUtils.isBlank(standardOutput)) {
            logger.error("Nothing returned from conan info command");
            return new Extraction.Builder().failure("Conan info command produced no output").build();
        }
        logger.trace(String.format("Parsing conan info output:\n%s", standardOutput));
        try {
            ConanDetectableResult result = conanInfoParser.generateCodeLocationFromConanInfoOutput(standardOutput, conanCliExtractorOptions.shouldIncludeDevDependencies());
            return new Extraction.Builder().success(result.getCodeLocation()).projectName(result.getProjectName()).projectVersion(result.getProjectVersion()).build();
        } catch (IntegrationException e) {
            return new Extraction.Builder().failure(e.getMessage()).build();
        }
    }
}
