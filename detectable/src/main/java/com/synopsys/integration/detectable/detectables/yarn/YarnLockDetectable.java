/**
 * hub-detect
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
package com.synopsys.integration.detectable.detectables.yarn;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.YarnResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class YarnLockDetectable extends Detectable {
    private static final String YARN_LOCK_FILENAME = "yarn.lock";

    private final FileFinder fileFinder;
    private final YarnResolver yarnResolver;
    private final YarnLockExtractor yarnLockExtractor;

    private File yarnLock;
    private File yarnExe;

    public YarnLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final YarnResolver yarnResolver, final YarnLockExtractor yarnLockExtractor) {
        super(environment, "Yarn Lock", "YARN");
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
        this.yarnResolver = yarnResolver;
    }

    @Override
    public DetectableResult applicable() {
        yarnLock = fileFinder.findFile(environment.getDirectory(), YARN_LOCK_FILENAME);
        if (yarnLock == null) {
            return new FileNotFoundDetectableResult(YARN_LOCK_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        yarnExe = yarnResolver.resolveYarn();

        if (yarnExe == null) {
            return new ExecutableNotFoundDetectableResult("yarn");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return yarnLockExtractor.extract(environment.getDirectory(), yarnLock, yarnExe);
    }

}
