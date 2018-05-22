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
package com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.Extractor;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.parse.NpmLockfilePackager;
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.npm.parse.NpmParseResult;

@Component
public class NpmLockfileExtractor extends Extractor<NpmLockfileContext> {

    @Autowired
    private NpmLockfilePackager npmLockfilePackager;

    @Autowired
    protected DetectConfiguration detectConfiguration;

    @Override
    public Extraction extract(final NpmLockfileContext context) {
        String lockText;
        try {
            lockText = FileUtils.readFileToString(context.lockfile, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }

        try {
            final boolean includeDev = detectConfiguration.getNpmIncludeDevDependencies();
            final NpmParseResult result = npmLockfilePackager.parse(context.directory.getCanonicalPath(), lockText, includeDev);
            return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
        } catch (final IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }
}
