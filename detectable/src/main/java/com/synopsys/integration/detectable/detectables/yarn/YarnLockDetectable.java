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
import com.synopsys.integration.detectable.detectable.executable.ExecutableType;
import com.synopsys.integration.detectable.detectable.executable.SystemExecutableFinder;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class YarnLockDetectable extends Detectable {
    private static final String YARN_LOCK_FILENAME = "yarn.lock";

    private final FileFinder fileFinder;
    private final SystemExecutableFinder systemExecutableFinder;
    private final YarnLockExtractor yarnLockExtractor;

    private File yarnlock;
    private File yarnExe;

    public YarnLockDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final SystemExecutableFinder systemExecutableFinder, final YarnLockExtractor yarnLockExtractor) {
        super(environment, "Yarn Lock", "YARN");
        this.fileFinder = fileFinder;
        this.yarnLockExtractor = yarnLockExtractor;
        this.systemExecutableFinder = systemExecutableFinder;
    }

    @Override
    public DetectableResult applicable() {
        yarnlock = fileFinder.findFile(environment.getDirectory(), YARN_LOCK_FILENAME);
        if (yarnlock == null) {
            return new FileNotFoundDetectableResult(YARN_LOCK_FILENAME);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        yarnExe = systemExecutableFinder.findExecutable(ExecutableType.YARN.getExecutable());

        if (yarnExe == null) {
            return new ExecutableNotFoundDetectableResult(ExecutableType.YARN.getExecutable());
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return yarnLockExtractor.extract(environment.getDirectory(), yarnlock, yarnExe);
    }

}
