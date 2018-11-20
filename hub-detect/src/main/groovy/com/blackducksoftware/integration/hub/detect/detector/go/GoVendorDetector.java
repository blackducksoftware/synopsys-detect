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
        vendorJson = fileFinder.findFile(environment.getDirectory(), VENDOR_JSON_FILENAME);
        if (vendorJson == null) {
            logger.debug(String.format("File %s not found", VENDOR_JSON_FILENAME));
            return new FileNotFoundDetectorResult(VENDOR_JSON_FILENAME);
        }
        File vendorJsonParentDir = vendorJson.getParentFile();
        if (!VENDOR_JSON_DIRNAME.equalsIgnoreCase(vendorJsonParentDir.getName())) {
            logger.debug(String.format("File %s found, but it was not under dir %s", VENDOR_JSON_FILENAME, VENDOR_JSON_DIRNAME));
            return new FileNotFoundDetectorResult(VENDOR_JSON_FILENAME);
        }

        logger.debug(String.format("%s/%s found", VENDOR_JSON_DIRNAME, VENDOR_JSON_FILENAME));
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
