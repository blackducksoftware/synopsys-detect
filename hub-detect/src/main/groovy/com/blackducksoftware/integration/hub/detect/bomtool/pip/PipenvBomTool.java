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

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FilesNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;

public class PipenvBomTool extends BomTool {
    public static final String SETUPTOOLS_DEFAULT_FILE_NAME = "setup.py";
    public static final String PIPFILE_FILE_NAME = "Pipfile";
    public static final String PIPFILE_DOT_LOCK_FILE_NAME = "Pipfile.lock";

    private final DetectFileFinder fileFinder;
    private final PythonExecutableFinder pythonExecutableFinder;
    private final PipenvExtractor pipenvExtractor;

    private String pythonExe;
    private String pipenvExe;
    private File pipfileDotLock;
    private File pipfile;
    private File setupFile;

    public PipenvBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final PythonExecutableFinder pythonExecutableFinder, final PipenvExtractor pipenvExtractor) {
        super(environment, "Pipenv Graph", BomToolGroupType.PIP, BomToolType.PIP_ENV);
        this.fileFinder = fileFinder;
        this.pipenvExtractor = pipenvExtractor;
        this.pythonExecutableFinder = pythonExecutableFinder;
    }

    @Override
    public BomToolResult applicable() {
        pipfile = fileFinder.findFile(environment.getDirectory(), PIPFILE_FILE_NAME);
        pipfileDotLock = fileFinder.findFile(environment.getDirectory(), PIPFILE_DOT_LOCK_FILE_NAME);

        if (pipfile != null || pipfileDotLock != null) {
            return new PassedBomToolResult();
        } else {
            return new FilesNotFoundBomToolResult(PIPFILE_FILE_NAME, PIPFILE_DOT_LOCK_FILE_NAME);
        }

    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        pythonExe = pythonExecutableFinder.findPython(environment);
        if (pythonExe == null) {
            return new ExecutableNotFoundBomToolResult("python");
        }

        pipenvExe = pythonExecutableFinder.findPipenv(environment);
        if (pipenvExe == null) {
            return new ExecutableNotFoundBomToolResult("pipenv");
        }

        setupFile = fileFinder.findFile(environment.getDirectory(), SETUPTOOLS_DEFAULT_FILE_NAME);

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pipenvExtractor.extract(this.getBomToolType(), environment.getDirectory(), pythonExe, pipenvExe, setupFile);
    }

}
