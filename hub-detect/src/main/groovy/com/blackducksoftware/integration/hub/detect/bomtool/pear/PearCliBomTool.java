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
package com.blackducksoftware.integration.hub.detect.bomtool.pear;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolEnvironment;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolGroupType;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;
import com.blackducksoftware.integration.hub.detect.bomtool.ExtractionId;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.BomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.ExecutableNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.bomtool.workflow.PassedBomToolResult;
import com.blackducksoftware.integration.hub.detect.exception.BomToolException;
import com.blackducksoftware.integration.hub.detect.util.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.StandardExecutableFinder.StandardExecutableType;

public class PearCliBomTool extends BomTool {
    public static final String PACKAGE_XML_FILENAME = "package.xml";

    private final DetectFileFinder fileFinder;
    private final StandardExecutableFinder standardExecutableFinder;
    private final PearCliExtractor pearCliExtractor;

    private File pearExe;

    public PearCliBomTool(final BomToolEnvironment environment, final DetectFileFinder fileFinder, final StandardExecutableFinder standardExecutableFinder, final PearCliExtractor pearCliExtractor) {
        super(environment, "Pear Cli", BomToolGroupType.PEAR, BomToolType.PEAR_CLI);
        this.fileFinder = fileFinder;
        this.standardExecutableFinder = standardExecutableFinder;
        this.pearCliExtractor = pearCliExtractor;
    }

    @Override
    public BomToolResult applicable() {
        final File PEAR = fileFinder.findFile(environment.getDirectory(), PACKAGE_XML_FILENAME);
        if (PEAR == null) {
            return new FileNotFoundBomToolResult(PACKAGE_XML_FILENAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() throws BomToolException {
        pearExe = standardExecutableFinder.getExecutable(StandardExecutableType.PEAR);

        if (pearExe == null) {
            return new ExecutableNotFoundBomToolResult("pear");
        }

        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        return pearCliExtractor.extract(this.getBomToolType(), environment.getDirectory(), pearExe);
    }

}
