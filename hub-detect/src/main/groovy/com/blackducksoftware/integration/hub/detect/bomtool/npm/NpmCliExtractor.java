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
package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class NpmCliExtractor {
    private final Logger logger = LoggerFactory.getLogger(NpmCliExtractor.class);

    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    private final ExecutableRunner executableRunner;
    private final NpmCliDependencyFinder npmCliDependencyFinder;
    private final DetectConfigWrapper detectConfigWrapper;

    public NpmCliExtractor(final ExecutableRunner executableRunner, final NpmCliDependencyFinder npmCliDependencyFinder, final DetectConfigWrapper detectConfigWrapper) {
        this.executableRunner = executableRunner;
        this.npmCliDependencyFinder = npmCliDependencyFinder;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory, final String npmExe, final ExtractionId extractionId) {

        final boolean includeDevDeps = detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES);
        final List<String> exeArgs = new ArrayList<>();
        exeArgs.add("ls");
        exeArgs.add("-json");
        if (!includeDevDeps) {
            exeArgs.add("-prod");
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
                final NpmParseResult result = npmCliDependencyFinder.generateCodeLocation(bomToolType, directory.getCanonicalPath(), standardOutput);
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
