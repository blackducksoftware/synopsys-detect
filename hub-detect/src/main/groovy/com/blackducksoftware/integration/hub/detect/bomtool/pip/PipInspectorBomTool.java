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
package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.InspectorNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class PipInspectorBomTool extends BomTool {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";

    private final DetectFileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipInspectorManager pipInspectorManager;
    private final PipInspectorExtractor pipInspectorExtractor;
    private final String requirementFilePath;

    String pythonExe;
    File pipInspector;
    File setupFile;

    public PipInspectorBomTool(final BomToolEnvironment environment, final String requirementFilePath, final DetectFileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipInspectorManager pipInspectorManager,
            final PipInspectorExtractor pipInspectorExtractor) {
        super(environment, "Pip Inspector", BomToolGroupType.PIP, BomToolType.PIP_INSPECTOR);
        this.fileFinder = fileFinder;
        this.pipInspectorExtractor = pipInspectorExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
        this.pipInspectorManager = pipInspectorManager;
        this.requirementFilePath = requirementFilePath;
    }

    @Override
    public BomToolResult applicable() {
        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);
        final boolean hasSetups = setupFile != null;
        final boolean hasRequirements = requirementFilePath != null && StringUtils.isNotBlank(requirementFilePath);
        if (hasSetups || hasRequirements) {
            return new PassedBomToolResult();
        } else {
            return new FileNotFoundBomToolResult(SETUPTOOLS_DEFAULT_FILE_NAME);
        }

    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundBomToolResult("python");
        }

        final String pipExe = pythonExecutableFinder.findPip(environment);
        if (pipExe == null) {
            return new ExecutableNotFoundBomToolResult("pip");
        }

        pipInspector = pipInspectorManager.findPipInspector(environment);
        if (pipInspector == null) {
            return new InspectorNotFoundBomToolResult("pip");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pipInspectorExtractor.extract(this.getBomToolType(), environment.getDirectory(), pythonExe, pipInspector, setupFile, requirementFilePath);
    }

}
