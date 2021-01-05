/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.go.vendor;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

@DetectableInfo(language = "Golang", forge = "GitHub", requirementsMarkdown = "File: vendor/vendor.json.")
public class GoVendorDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String VENDOR_JSON_DIRNAME = "vendor";
    private static final String VENDOR_JSON_FILENAME = "vendor.json";

    private final FileFinder fileFinder;
    private final GoVendorExtractor goVendorExtractor;

    private File vendorJson;

    public GoVendorDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final GoVendorExtractor goVendorExtractor) {
        super(environment);
        this.fileFinder = fileFinder;
        this.goVendorExtractor = goVendorExtractor;
    }

    @Override
    public DetectableResult applicable() {
        final File vendorDir = fileFinder.findFile(environment.getDirectory(), VENDOR_JSON_DIRNAME);
        if (vendorDir == null) {
            logger.trace(String.format("Dir %s not found", VENDOR_JSON_DIRNAME));
            return new FileNotFoundDetectableResult(VENDOR_JSON_FILENAME);
        }
        vendorJson = fileFinder.findFile(vendorDir, VENDOR_JSON_FILENAME);
        if (vendorJson == null) {
            logger.trace(String.format("File %s not found", VENDOR_JSON_FILENAME));
            return new FileNotFoundDetectableResult(VENDOR_JSON_FILENAME);
        }
        logger.trace(String.format("%s/%s found", VENDOR_JSON_DIRNAME, VENDOR_JSON_FILENAME));
        return new PassedDetectableResult();
    }

    @Override
    public DetectableResult extractable() {
        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return goVendorExtractor.extract(vendorJson);
    }
}
