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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.parse.NpmCliDependencyFinder;
import com.blackducksoftware.integration.hub.detect.bomtool.npm.parse.NpmParseResult;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.Executable;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

@Component
public class NpmCliExtractor {
    private final Logger logger = LoggerFactory.getLogger(NpmCliExtractor.class);

    public static final String OUTPUT_FILE = "detect_npm_proj_dependencies.json";
    public static final String ERROR_FILE = "detect_npm_error.json";

    @Autowired
    private DetectConfiguration detectConfiguration;

    @Autowired
    private ExecutableRunner executableRunner;

    @Autowired
    private DetectFileManager detectFileManager;

    @Autowired
    private NpmCliDependencyFinder npmCliDependencyFinder;

    public Extraction extract(final BomToolType bomToolType, final File directory, final String npmExe, final ExtractionId extractionId) {

        final File npmLsOutputFile = detectFileManager.getOutputFile(extractionId, NpmCliExtractor.OUTPUT_FILE);
        final File npmLsErrorFile = detectFileManager.getOutputFile(extractionId, NpmCliExtractor.ERROR_FILE);

        final boolean includeDevDeps = detectConfiguration.getNpmIncludeDevDependencies();
        final List<String> exeArgs = Arrays.asList("ls", "-json");
        if (!includeDevDeps) {
            exeArgs.add("-prod");
        }

        final Executable npmLsExe = new Executable(directory, npmExe, exeArgs);
        try {
            executableRunner.executeToFile(npmLsExe, npmLsOutputFile, npmLsErrorFile);
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }

        if (npmLsOutputFile.length() > 0) {
            if (npmLsErrorFile.length() > 0) {
                logger.debug("Error when running npm ls -json command");
                printFileToDebug(npmLsErrorFile);
                return new Extraction.Builder().failure("Npm returned no output after runnin npm ls.").build();
            }
            logger.debug("Parsing npm ls file.");
            printFileToDebug(npmLsOutputFile);
            try {
                final NpmParseResult result = npmCliDependencyFinder.generateCodeLocation(bomToolType, directory.getCanonicalPath(), npmLsOutputFile);
                return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
            } catch (final IOException e) {
                return new Extraction.Builder().exception(e).build();
            }

        } else if (npmLsErrorFile.length() > 0) {
            logger.error("Error when running npm ls -json command");
            printFileToDebug(npmLsErrorFile);
            return new Extraction.Builder().failure("Npm returned error after running npm ls.").build();
        } else {
            logger.warn("Nothing returned from npm ls -json command");
            return new Extraction.Builder().failure("Npm returned nothing after running npm ls").build();
        }

    }

    void printFileToDebug(final File errorFile) {
        String text = "";
        try {
            for (final String line : Files.readAllLines(errorFile.toPath(), StandardCharsets.UTF_8)) {
                text += line + System.lineSeparator();
            }
        } catch (final IOException e) {
            logger.debug("Failed to read NPM error file.");
        }
        logger.debug(text);
    }

}
