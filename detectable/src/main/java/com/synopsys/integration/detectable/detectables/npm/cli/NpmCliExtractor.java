/**
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
package com.synopsys.integration.detectable.detectables.npm.cli;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectables.npm.cli.parse.NpmCliParser;
import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmParseResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.executable.ExecutableOutput;

public class NpmCliExtractor {
    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableExecutableRunner executableRunner;
    private final NpmCliParser npmCliParser;

    public NpmCliExtractor(DetectableExecutableRunner executableRunner, NpmCliParser npmCliParser) {
        this.executableRunner = executableRunner;
        this.npmCliParser = npmCliParser;
    }

    public Extraction extract(File directory, ExecutableTarget npmExe, NpmCliExtractorOptions npmCliExtractorOptions) {//TODO: Extractor should not use DetectableOptions

        boolean includeDevDeps = npmCliExtractorOptions.shouldIncludeDevDependencies();
        List<String> exeArgs = new ArrayList<>();
        exeArgs.add("ls");
        exeArgs.add("-json");
        if (!includeDevDeps) {
            exeArgs.add("-prod");
        }

        npmCliExtractorOptions.getNpmArguments()
            .map(arg -> arg.split(" "))
            .ifPresent(additionalArguments -> exeArgs.addAll(Arrays.asList(additionalArguments)));

        ExecutableOutput npmLsOutput;
        try {
            npmLsOutput = executableRunner.execute(ExecutableUtils.createFromTarget(directory, npmExe, exeArgs));
        } catch (Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
        String standardOutput = npmLsOutput.getStandardOutput();
        String errorOutput = npmLsOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.error("Error when running npm ls -json command");
            logger.error(errorOutput);
            return new Extraction.Builder().failure("Npm wrote to stderr while running npm ls.").build();
        } else if (StringUtils.isNotBlank(standardOutput)) {
            logger.debug("Parsing npm ls file.");
            logger.debug(standardOutput);
            NpmParseResult result = npmCliParser.generateCodeLocation(standardOutput);
            return new Extraction.Builder().success(result.getCodeLocation()).projectName(result.getProjectName()).projectVersion(result.getProjectVersion()).build();
        } else {
            logger.error("Nothing returned from npm ls -json command");
            return new Extraction.Builder().failure("Npm returned error after running npm ls.").build();
        }
    }
}
