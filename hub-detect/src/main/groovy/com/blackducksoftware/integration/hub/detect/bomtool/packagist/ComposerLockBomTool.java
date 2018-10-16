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

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;

public class ComposerLockBomTool extends BomTool {
    public static final String COMPOSER_LOCK = "composer.lock";
    public static final String COMPOSER_JSON = "composer.json";

    private final DetectFileFinder fileFinder;
    private final ComposerLockExtractor composerLockExtractor;

    File composerLock;
    File composerJson;

    public ComposerLockBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final ComposerLockExtractor composerLockExtractor) {
        super(environment, "Composer Lock", BomToolGroupType.PACKAGIST, BomToolType.COMPOSER_LOCK);
        this.fileFinder = fileFinder;
        this.composerLockExtractor = composerLockExtractor;
    }

    @Override
    public BomToolResult applicable() {
        composerLock = fileFinder.findFile(environment.getDirectory(), COMPOSER_LOCK);
        if (composerLock == null) {
            return new FileNotFoundBomToolResult(COMPOSER_LOCK);
        }

        composerJson = fileFinder.findFile(environment.getDirectory(), COMPOSER_JSON);
        if (composerJson == null) {
            return new FileNotFoundBomToolResult(COMPOSER_JSON);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() {
        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return composerLockExtractor.extract(this.getBomToolType(), environment.getDirectory(), composerJson, composerLock);
    }

}
