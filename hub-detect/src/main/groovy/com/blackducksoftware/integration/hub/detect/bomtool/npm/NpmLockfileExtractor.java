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

import org.apache.commons.io.FileUtils;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class NpmLockfileExtractor {
    private final NpmLockfilePackager npmLockfilePackager;
    private final DetectConfigWrapper detectConfigWrapper;

    public NpmLockfileExtractor(final NpmLockfilePackager npmLockfilePackager, final DetectConfigWrapper detectConfigWrapper) {
        this.npmLockfilePackager = npmLockfilePackager;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public Extraction extract(final BomToolType bomToolType, final File directory, final File lockfile) {
        String lockText;
        try {
            lockText = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        try {
            final boolean includeDevDeps = detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_NPM_INCLUDE_DEV_DEPENDENCIES);
            final NpmParseResult result = npmLockfilePackager.parse(bomToolType, directory.getCanonicalPath(), lockText, includeDevDeps);
            return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
