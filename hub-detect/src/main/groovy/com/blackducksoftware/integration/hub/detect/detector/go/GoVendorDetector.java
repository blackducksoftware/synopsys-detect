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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;

public class GoVendorDetector extends Detector {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String VENDOR_JSON_DIRNAME = "vendor";
    public static final String VENDOR_JSON_FILENAME = "vendor.json";

    private final DetectFileFinder fileFinder;
    private final GoVendorExtractor goVendorExtractor;

    private File vendorJson;

    public GoVendorDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final GoVendorExtractor goVendorExtractor) {
        super(environment, "Go Vendor", DetectorType.GO_VENDOR);
        this.fileFinder = fileFinder;
        this.goVendorExtractor = goVendorExtractor;
    }

    @Override
    public DetectorResult applicable() {
        File vendorDir  = fileFinder.findFile(environment.getDirectory(), VENDOR_JSON_DIRNAME);
        if (vendorDir == null) {
            logger.trace(String.format("Dir %s not found", VENDOR_JSON_DIRNAME));
            return new FileNotFoundDetectorResult(VENDOR_JSON_FILENAME);
        }
        vendorJson = fileFinder.findFile(vendorDir, VENDOR_JSON_FILENAME);
        if (vendorJson == null) {
            logger.trace(String.format("File %s not found", VENDOR_JSON_FILENAME));
            return new FileNotFoundDetectorResult(VENDOR_JSON_FILENAME);
        }
        logger.trace(String.format("%s/%s found", VENDOR_JSON_DIRNAME, VENDOR_JSON_FILENAME));
        return new PassedDetectorResult();
    }

    @Override
    public DetectorResult extractable() {
        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        addRelevantDiagnosticFile(vendorJson);
        return goVendorExtractor.extract(environment.getDirectory(), vendorJson);
    }
}
