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
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.BomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundBomToolResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedBomToolResult;

public class GoVndrDetector extends Detector {
    public static final String VNDR_CONF_FILENAME = "vendor.conf";

    private final DetectFileFinder fileFinder;
    private final GoVndrExtractor goVndrExtractor;

    private File vndrConfig;

    public GoVndrDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final GoVndrExtractor goVndrExtractor) {
        super(environment, "Vendor Config", DetectorType.GO_VNDR);
        this.fileFinder = fileFinder;
        this.goVndrExtractor = goVndrExtractor;
    }

    @Override
    public BomToolResult applicable() {
        vndrConfig = fileFinder.findFile(environment.getDirectory(), VNDR_CONF_FILENAME);
        if (vndrConfig == null) {
            return new FileNotFoundBomToolResult(VNDR_CONF_FILENAME);
        }

        return new PassedBomToolResult();
    }

    @Override
    public BomToolResult extractable() {
        return new PassedBomToolResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        addRelevantDiagnosticFile(vndrConfig);
        return goVndrExtractor.extract(environment.getDirectory(), vndrConfig);
    }

}
