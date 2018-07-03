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
package com.blackducksoftware.integration.hub.detect.bomtool.clang;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;

public class CLangBomTool extends BomTool {
    private static final String JSON_COMPILATION_DATABASE_FILENAME = "compile_commands.json";
    private final CLangExtractor cLangExtractor;
    private File jsonCompilationDatabaseFile = null;
    private final DetectFileFinder fileFinder;

    public CLangBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final CLangExtractor cLangExtractor) {
        super(environment, "Clang", BomToolGroupType.CLANG, BomToolType.CLANG);
        this.fileFinder = fileFinder;
        this.cLangExtractor = cLangExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File jsonCompilationDatabaseFile = fileFinder.findFile(environment.getDirectory(), JSON_COMPILATION_DATABASE_FILENAME);
        if (jsonCompilationDatabaseFile == null) {
            return new FileNotFoundBomToolResult(JSON_COMPILATION_DATABASE_FILENAME);
        }
        this.jsonCompilationDatabaseFile = jsonCompilationDatabaseFile;
        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return cLangExtractor.extract(environment.getDirectory(), environment.getDepth(), extractionId, jsonCompilationDatabaseFile);
    }

}
