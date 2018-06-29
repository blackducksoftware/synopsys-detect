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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder.StandardExecutableType;

public class CpanCliBomTool extends BomTool {
    public static final String MAKEFILE = "Makefile.PL";

    private final DetectFileFinder fileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final CpanCliExtractor cpanCliExtractor;

    private File cpanExe;
    private File cpanmExe;

    public CpanCliBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final CpanCliExtractor cpanCliExtractor) {
        super(environment, "Cpan Cli", BomToolGroupType.CPAN, BomToolType.CPAN_CLI);
        this.fileFinder = fileFinder;
        this.cpanCliExtractor = cpanCliExtractor;
        this.standardExecutableFinder = standardExecutableFinder;
    }

    @Override
    public BomToolResult applicable() {
        final File makeFile = fileFinder.findFile(environment.getDirectory(), MAKEFILE);
        if (makeFile == null) {
            return new FileNotFoundBomToolResult(MAKEFILE);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        final File cpan = standardExecutableFinder.getExecutable(StandardExecutableType.CPAN);

        if (cpan == null) {
            return new ExecutableNotFoundBomToolResult("cpan");
        } else {
            cpanExe = cpan;
        }

        final File cpanm = standardExecutableFinder.getExecutable(StandardExecutableType.CPANM);

        if (cpanm == null) {
            return new ExecutableNotFoundBomToolResult("cpanm");
        } else {
            cpanmExe = cpanm;
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return cpanCliExtractor.extract(this.getBomToolType(), environment.getDirectory(), cpanExe, cpanmExe);
    }

}
