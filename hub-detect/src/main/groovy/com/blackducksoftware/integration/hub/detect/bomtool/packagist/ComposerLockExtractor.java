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
package com.blackducksoftware.integration.hub.detect.bomtool.packagist;

import java.io.File;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.parse.PackagistParseResult;
import com.blackducksoftware.integration.hub.detect.bomtool.packagist.parse.PackagistParser;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;

@Component
public class ComposerLockExtractor {

    @Autowired
    PackagistParser packagistParser;

    public Extraction extract(final BomToolType bomToolType, final File directory, final File composerJson, final File composerLock) {
        try {
            final String composerJsonText = FileUtils.readFileToString(composerJson, StandardCharsets.UTF_8);
            final String composerLockText = FileUtils.readFileToString(composerLock, StandardCharsets.UTF_8);

            final PackagistParseResult result = packagistParser.getDependencyGraphFromProject(bomToolType, directory.toString(), composerJsonText, composerLockText);

            return new Extraction.Builder().success(result.codeLocation).projectName(result.projectName).projectVersion(result.projectVersion).build();
        } catch (final Exception e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

}
