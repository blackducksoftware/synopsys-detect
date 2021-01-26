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
package com.synopsys.integration.detectable.detectables.packagist;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectables.packagist.model.PackagistParseResult;
import com.synopsys.integration.detectable.detectables.packagist.parse.PackagistParser;

public class ComposerLockExtractor {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final PackagistParser packagistParser;

    public ComposerLockExtractor(final PackagistParser packagistParser) {
        this.packagistParser = packagistParser;
    }

    public Extraction extract(final File composerJson, final File composerLock, boolean includeDevDependencies) {
        try {
            final String composerJsonText = FileUtils.readFileToString(composerJson, StandardCharsets.UTF_8);
            final String composerLockText = FileUtils.readFileToString(composerLock, StandardCharsets.UTF_8);

            logger.debug(composerJsonText);
            logger.debug(composerLockText);

            final PackagistParseResult result = packagistParser.getDependencyGraphFromProject(composerJsonText, composerLockText, includeDevDependencies);

            return new Extraction.Builder()
                       .success(result.getCodeLocation())
                       .projectName(result.getProjectName())
                       .projectVersion(result.getProjectVersion())
                       .build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
