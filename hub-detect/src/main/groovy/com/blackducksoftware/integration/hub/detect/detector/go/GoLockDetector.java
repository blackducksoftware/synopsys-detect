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
package com.blackducksoftware.integration.hub.detect.detector.go;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.CacheableExecutableFinder.CacheableExecutableType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.InspectorNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;

public class GoLockDetector extends Detector {
    public static final String GOPKG_LOCK_FILENAME = "Gopkg.lock";

    private final DetectFileFinder fileFinder;
    private final GoInspectorManager goInspectorManager;
    private final CacheableExecutableFinder cacheableExecutableFinder;
    private final GoDepExtractor goDepExtractor;

    private File goExe;
    private String goDepInspector;

    public GoLockDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final CacheableExecutableFinder cacheableExecutableFinder, final GoInspectorManager goInspectorManager,
        final GoDepExtractor goDepExtractor) {
        super(environment, "Go Lock", DetectorType.GO_DEP);
        this.fileFinder = fileFinder;
        this.goInspectorManager = goInspectorManager;
        this.cacheableExecutableFinder = cacheableExecutableFinder;
        this.goDepExtractor = goDepExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File lock = fileFinder.findFile(environment.getDirectory(), GOPKG_LOCK_FILENAME);
        if (lock == null) {
            return new FileNotFoundBomToolResult(GOPKG_LOCK_FILENAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws DetectorException {
        goExe = cacheableExecutableFinder.getExecutable(CacheableExecutableType.GO);
        if (goExe == null) {
            return new ExecutableNotFoundBomToolResult("go");
        }

        goDepInspector = goInspectorManager.evaluate();
        if (goDepInspector == null) {
            return new InspectorNotFoundBomToolResult("go");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return goDepExtractor.extract(environment.getDirectory(), goExe, goDepInspector);
    }

}
