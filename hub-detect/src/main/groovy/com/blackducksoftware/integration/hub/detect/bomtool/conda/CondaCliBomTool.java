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
package com.blackducksoftware.integration.hub.detect.bomtool.conda;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.result.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.evaluation.BomToolException;
import com.blackducksoftware.integration.hub.detect.extraction.model.Extraction;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.extraction.model.StandardExecutableFinder.StandardExecutableType;
import com.blackducksoftware.integration.hub.detect.model.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;

public class CondaCliBomTool extends BomTool {
    public static final String ENVIRONEMNT_YML = "environment.yml";

    private final DetectFileFinder fileFinder;
    private StandardExecutableFinder standardExecutableFinder;
    private final CondaCliExtractor condaExtractor;

    private File condaExe;

    public CondaCliBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final CondaCliExtractor condaExtractor) {
        super(environment, "Conda Cli", BomToolGroupType.CONDA, BomToolType.CONDA_CLI);
        this.fileFinder = fileFinder;
        this.condaExtractor = condaExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File ymlFile = fileFinder.findFile(environment.getDirectory(), ENVIRONEMNT_YML);
        if (ymlFile == null) {
            return new FileNotFoundBomToolResult(ENVIRONEMNT_YML);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        condaExe = standardExecutableFinder.getExecutable(StandardExecutableType.CONDA);

        if (condaExe == null) {
            return new ExecutableNotFoundBomToolResult("conda");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return condaExtractor.extract(this.getBomToolType(), environment.getDirectory(), condaExe);
    }

}
