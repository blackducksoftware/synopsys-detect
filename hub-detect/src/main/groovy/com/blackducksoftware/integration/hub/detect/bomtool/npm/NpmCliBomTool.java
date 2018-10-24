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
package com.blackducksoftware.integration.hub.detect.bomtool.npm;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolException;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.NpmRunInstallBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;

public class NpmCliBomTool extends BomTool {
    public static final String NODE_MODULES = "node_modules";
    public static final String PACKAGE_JSON = "package.json";

    private final DetectFileFinder fileFinder;
    private final NpmExecutableFinder npmExecutableFinder;
    private final NpmCliExtractor npmCliExtractor;

    private String npmExe;

    public NpmCliBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final NpmExecutableFinder npmExecutableFinder, final NpmCliExtractor npmCliExtractor) {
        super(environment, "Npm Cli", BomToolGroupType.NPM, BomToolType.NPM_CLI);
        this.fileFinder = fileFinder;
        this.npmExecutableFinder = npmExecutableFinder;
        this.npmCliExtractor = npmCliExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File packageJson = fileFinder.findFile(environment.getDirectory(), PACKAGE_JSON);
        if (packageJson == null) {
            return new FileNotFoundBomToolResult(PACKAGE_JSON);
        }

        addRelevantDiagnosticFile(packageJson);
        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        final File nodeModules = fileFinder.findFile(environment.getDirectory(), NODE_MODULES);
        if (nodeModules == null) {
            return new NpmRunInstallBomToolResult(environment.getDirectory().getAbsolutePath());
        }

        npmExe = npmExecutableFinder.findNpm(environment);
        if (npmExe == null) {
            return new ExecutableNotFoundBomToolResult("npm");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return npmCliExtractor.extract(this.getBomToolType(), environment.getDirectory(), npmExe, extractionId);
    }

}
