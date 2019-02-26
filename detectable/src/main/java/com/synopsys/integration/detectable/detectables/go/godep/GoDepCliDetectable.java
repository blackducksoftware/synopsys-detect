/**
 * detectable
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
package com.synopsys.integration.detector.detector.go;

import java.io.File;
import java.util.List;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.go.GoDepResolver;
import com.synopsys.integration.detectable.detectable.inspector.go.GoResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExecutableNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.detectables.go.godep.GoDepExtractor;

public class GoDepCliDetectable extends Detectable {
    public static final String GOFILE_FILENAME_PATTERN = "*.go";

    private final FileFinder fileFinder;
    private final GoDepResolver goDepResolver;
    private final GoResolver goResolver;
    private final GoDepExtractor goDepExtractor;

    private File goExe;
    private File goDepInspector;

    public GoDepCliDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GoResolver goResolver, final GoDepResolver goDepResolver, final GoDepExtractor goDepExtractor) {
        super(environment, "Go Dep Cli", "Go Dep");
        this.fileFinder = fileFinder;
        this.goResolver = goResolver;
        this.goDepResolver = goDepResolver;
        this.goDepExtractor = goDepExtractor;
    }

    @Override
    public DetectableResult applicable() {
        final List<File> found = fileFinder.findFiles(environment.getDirectory(), GOFILE_FILENAME_PATTERN);
        if (found == null || found.size() == 0) {
            return new FileNotFoundDetectableResult(GOFILE_FILENAME_PATTERN);
        }

        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        goExe = goResolver.resolveGo();
        if (goExe == null) {
            return new ExecutableNotFoundDetectableResult("go");
        }

        goDepInspector = goDepResolver.resolveGoDep(environment.getDirectory());
        if (goDepInspector == null) {
            return new InspectorNotFoundDetectableResult("go dep");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return goDepExtractor.extract(environment.getDirectory(), goExe, goDepInspector);
    }
}
