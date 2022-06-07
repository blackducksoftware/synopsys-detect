package com.synopsys.integration.detectable.detectables.go.vendor;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detectable.detectable.Requirements;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;

@DetectableInfo(name = "Go Vendor", language = "Golang", forge = "GitHub", accuracy = DetectableAccuracyType.HIGH, requirementsMarkdown = "File: vendor/vendor.json.")
public class GoVendorDetectable extends Detectable {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String VENDOR_JSON_DIRNAME = "vendor";
    private static final String VENDOR_JSON_FILENAME = "vendor.json";

    private final FileFinder fileFinder;
    private final GoVendorExtractor goVendorExtractor;

    private File vendorJson;

    public GoVendorDetectable(DetectableEnvironment environment, FileFinder fileFinder, GoVendorExtractor goVendorExtractor) {
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
    public Extraction extract(ExtractionEnvironment extractionEnvironment) {
        return goVendorExtractor.extract(vendorJson);
    }
}
