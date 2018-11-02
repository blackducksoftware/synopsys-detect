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
package com.blackducksoftware.integration.hub.detect.detector.npm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class NpmCliExtractor {
    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";
    private final Logger logger = LoggerFactory.getLogger(NpmCliExtractor.class);
    private final ExecutableRunner executableRunner;
    private final NpmCliParser npmCliParser;
    private final DetectConfiguration detectConfiguration;

    public NpmCliExtractor(final ExecutableRunner executableRunner, final NpmCliParser npmCliParser, final DetectConfiguration detectConfiguration) {
        this.executableRunner = executableRunner;
        this.npmCliParser = npmCliParser;
        this.detectConfiguration = detectConfiguration;
    }

    public Extraction extract(final File directory, final String npmExe, final ExtractionId extractionId) {

        final boolean includeDevDeps = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES, PropertyAuthority.None);
        final List<String> exeArgs = new ArrayList<>();
        exeArgs.add("ls");
        exeArgs.add("-json");
        if (!includeDevDeps) {
            exeArgs.add("-prod");
        }

        final String additionalArguments = detectConfiguration.getProperty(DetectProperty.DETECT_NPM_ARGUMENTS, PropertyAuthority.None);
        if (StringUtils.isNotBlank(additionalArguments)) {
            exeArgs.addAll(Arrays.asList(additionalArguments.split(" ")));
        }

        final Executable npmLsExe = new Executable(directory, npmExe, exeArgs);
        ExecutableOutput executableOutput;
        try {
            executableOutput = executableRunner.execute(npmLsExe);
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
        final String standardOutput = executableOutput.getStandardOutput();
        final String errorOutput = executableOutput.getErrorOutput();
        if (StringUtils.isNotBlank(errorOutput)) {
            logger.error("Error when running npm ls -json command");
            logger.error(errorOutput);
            return new Extraction.Builder().failure("Npm wrote to stderr while running npm ls.").build();
        } else if (StringUtils.isNotBlank(standardOutput)) {
            logger.debug("Parsing npm ls file.");
            logger.debug(standardOutput);
            try {
                final NpmParseResult result = npmCliParser.generateCodeLocation(directory.getCanonicalPath(), standardOutput);
                return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
            } catch (final IOException e) {
                return new Extraction.Builder().exception(e).build();
            }
        } else {
            logger.error("Nothing returned from npm ls -json command");
            return new Extraction.Builder().failure("Npm returned error after running npm ls.").build();
        }
    }
}
