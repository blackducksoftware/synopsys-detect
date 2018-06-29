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
package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoDepExtractor;
import com.blackducksoftware.integration.hub.detect.bomtool.go.extraction.GoInspectorManager;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.InspectorNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder.StandardExecutableType;

public class GoCliBomTool extends BomTool {
    public static final String GOFILE_FILENAME_PATTERN = "*.go";

    private final DetectFileFinder fileFinder;
    private final GoInspectorManager goInspectorManager;
    private final StandardExecutableFinder standardExecutableFinder;
    private final GoDepExtractor goDepExtractor;

    private File goExe;
    private String goDepInspector;

    public GoCliBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final GoInspectorManager goInspectorManager, final GoDepExtractor goDepExtractor) {
        super(environment, "Go Cli", BomToolGroupType.GO_DEP, BomToolType.GO_CLI);
        this.fileFinder = fileFinder;
        this.standardExecutableFinder = standardExecutableFinder;
        this.goInspectorManager = goInspectorManager;
        this.goDepExtractor = goDepExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final List<File> found = fileFinder.findFiles(environment.getDirectory(), GOFILE_FILENAME_PATTERN);
        if (found == null || found.size() == 0) {
            return new FileNotFoundBomToolResult(GOFILE_FILENAME_PATTERN);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        goExe = standardExecutableFinder.getExecutable(StandardExecutableType.GO);
        if (goExe == null) {
            return new ExecutableNotFoundBomToolResult("go");
        }

        goDepInspector = goInspectorManager.evaluate(environment);
        if (goDepInspector == null) {
            return new InspectorNotFoundBomToolResult("go");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return goDepExtractor.extract(this.getBomToolType(), environment.getDirectory(), goExe, goDepInspector);
    }

}
