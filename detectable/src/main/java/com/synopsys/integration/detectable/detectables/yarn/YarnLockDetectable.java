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
package com.synopsys.integration.detectable.detectables.yarn;

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

@DetectableInfo(language = "Node JS", forge = "npmjs", requirementsMarkdown = "Files: yarn.lock and package.json.")
public class YarnLockDetectable extends Detectable {
    public static final String YARN_LOCK_FILENAME = "yarn.lock";
    private static final String YARN_PACKAGE_JSON = "package.json";

    private final FileFinder fileFinder;
    private final YarnLockExtractor yarnLockExtractor;

    private File yarnLock;
    private File packageJson;

    public YarnLockDetectable(DetectableEnvironment environment, FileFinder fileFinder, YarnLockExtractor yarnLockExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
    }

    @Override
    public DetectableResult applicable() {
        yarnLock = fileFinder.findFile(environment.getDirectory(), YARN_LOCK_FILENAME);
        if (yarnLock == null) {
            return new FileNotFoundDetectableResult(YARN_LOCK_FILENAME);
        }

        packageJson = fileFinder.findFile(environment.getDirectory(), YARN_PACKAGE_JSON);
        if (packageJson == null) {
            return new FileNotFoundDetectableResult(YARN_PACKAGE_JSON);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return yarnLockExtractor.extract(environment.getDirectory(), yarnLock, packageJson);
    }
}
