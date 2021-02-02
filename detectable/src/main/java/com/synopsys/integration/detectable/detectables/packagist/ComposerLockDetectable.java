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

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "PHP", forge = "Packagist.org", requirementsMarkdown = "Files: composer.lock, composer.json.")
public class ComposerLockDetectable extends Detectable {
    private static final String COMPOSER_LOCK = "composer.lock";
    private static final String COMPOSER_JSON = "composer.json";

    private final FileFinder fileFinder;
    private final ComposerLockExtractor composerLockExtractor;
    private final ComposerLockDetectableOptions composerLockDetectableOptions;

    private File composerLock;
    private File composerJson;

    public ComposerLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final ComposerLockExtractor composerLockExtractor, ComposerLockDetectableOptions composerLockDetectableOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.composerLockExtractor = composerLockExtractor;
        this.composerLockDetectableOptions = composerLockDetectableOptions;
    }

    @Override
    public DetectableResult applicable() {
        composerLock = fileFinder.findFile(environment.getDirectory(), COMPOSER_LOCK);
        if (composerLock == null) {
            return new FileNotFoundDetectableResult(COMPOSER_LOCK);
        }

        composerJson = fileFinder.findFile(environment.getDirectory(), COMPOSER_JSON);
        if (composerJson == null) {
            return new FileNotFoundDetectableResult(COMPOSER_JSON);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return composerLockExtractor.extract(composerJson, composerLock, composerLockDetectableOptions.shouldIncludeDevDependencies());
    }

}
