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
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.io.File;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.resolver.GoResolver;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(language = "Golang", forge = "Go Modules", requirementsMarkdown = "File: go.mod.<br/><br/>Executable: go.")
public class GoModCliDetectable extends Detectable {
    public static final String GOMOD_FILENAME_PATTERN = "go.mod";

    private final FileFinder fileFinder;
    private final GoResolver goResolver;
    private final GoModCliExtractor goModCliExtractor;

    private ExecutableTarget goExe;

    public GoModCliDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoResolver goResolver, GoModCliExtractor goModCliExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goResolver = goResolver;
        this.goModCliExtractor = goModCliExtractor;
    }

    @Override
    public DetectableResult applicable() {
        File found = fileFinder.findFile(environment.getDirectory(), GOMOD_FILENAME_PATTERN);
        if (found == null) {
            return new FileNotFoundDetectableResult(GOMOD_FILENAME_PATTERN);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        goExe = goResolver.resolveGo();
        if (goExe == null) {
            return new ExecutableNotFoundDetectableResult("go");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return goModCliExtractor.extract(environment.getDirectory(), goExe);
    }
}
