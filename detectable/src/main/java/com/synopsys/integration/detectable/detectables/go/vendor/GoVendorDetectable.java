/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.vendor;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

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
        Requirements requirements = new Requirements(fileFinder, environment);
        File vendorDir = requirements.directory(VENDOR_JSON_DIRNAME);
        vendorJson = requirements.file(vendorDir, VENDOR_JSON_FILENAME);
        return requirements.result();
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
