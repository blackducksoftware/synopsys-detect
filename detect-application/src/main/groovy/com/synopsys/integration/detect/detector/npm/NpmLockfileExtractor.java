/**
 * detect-application
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.detector.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.synopsys.integration.detect.configuration.DetectConfiguration;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detect.configuration.PropertyAuthority;
import com.synopsys.integration.detect.workflow.extraction.Extraction;

public class NpmLockfileExtractor {
    private final NpmLockfileParser npmLockfileParser;
    private final DetectConfiguration detectConfiguration;

    public NpmLockfileExtractor(final NpmLockfileParser npmLockfileParser, final DetectConfiguration detectConfiguration) {
        this.npmLockfileParser = npmLockfileParser;
        this.detectConfiguration = detectConfiguration;
    }

    public Extraction extract(final File directory, final File lockfile, final Optional<File> packageJson) {
        try {
            final boolean includeDev = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES, PropertyAuthority.None);

            String lockText = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
            Optional<String> packageText = Optional.empty();
            if (packageJson.isPresent()) {
                packageText = Optional.of(FileUtils.readFileToString(packageJson.get(), StandardCharsets.UTF_8));
            }

            final NpmParseResult result = npmLockfileParser.parse(directory.getCanonicalPath(), packageText, lockText, includeDev);

            return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();

        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
