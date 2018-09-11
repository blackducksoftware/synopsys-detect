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
package com.blackducksoftware.integration.hub.detect.bomtool.hex;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.util.executable.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.util.executable.StandardExecutableFinder.StandardExecutableType;

public class RebarBomTool extends BomTool {
    public static final String REBAR_CONFIG = "rebar.config";

    private final DetectFileFinder fileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final RebarExtractor rebarExtractor;

    private File rebarExe;

    public RebarBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final RebarExtractor rebarExtractor) {
        super(environment, "Rebar Config", BomToolGroupType.HEX, BomToolType.REBAR);
        this.fileFinder = fileFinder;
        this.rebarExtractor = rebarExtractor;
        this.standardExecutableFinder = standardExecutableFinder;
    }

    @Override
    public BomToolResult applicable() {
        final File rebar = fileFinder.findFile(environment.getDirectory(), REBAR_CONFIG);
        if (rebar == null) {
            return new FileNotFoundBomToolResult(REBAR_CONFIG);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        rebarExe = standardExecutableFinder.getExecutable(StandardExecutableType.REBAR3);

        if (rebarExe == null) {
            return new ExecutableNotFoundBomToolResult("rebar");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return rebarExtractor.extract(this.getBomToolType(), environment.getDirectory(), rebarExe);
    }

}
