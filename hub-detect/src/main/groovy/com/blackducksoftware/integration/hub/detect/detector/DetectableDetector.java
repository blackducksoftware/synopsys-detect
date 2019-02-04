package com.blackducksoftware.integration.hub.detect.detector;
/*
import java.io.File;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.detector.bitbake.BitbakeDetectorOptions;
import com.blackducksoftware.integration.hub.detect.detector.bitbake.BitbakeExtractor;
import com.blackducksoftware.integration.hub.detect.util.executable.CacheableExecutableFinder;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.file.DetectFileFinder;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectableDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.ExecutableNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.FileNotFoundDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PassedDetectorResult;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.PropertyInsufficientDetectorResult;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class DetectableDetector extends Detector {
    private final Detectable detectable;

    public DetectableDetector(Detectable detectable, final DetectorEnvironment environment, final String name, final DetectorType detectorType) {
        super(environment, name, detectorType);
        this.detectable = detectable;
    }

    @Override
    public DetectorResult applicable() {
        return new DetectableDetectorResult(detectable.applicable());
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        try {
            return new DetectableDetectorResult(detectable.extractable());
        } catch (DetectableException e){
            throw new DetectorException(e);
        }
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        File outputDirectory = new File("");//TODO: FIX THIS PLEASE OH GOD
        ExtractionEnvironment extractionEnvironment = new ExtractionEnvironment(outputDirectory);
        return detectable.extract(extractionEnvironment);
    }
}
*/