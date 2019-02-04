package com.blackducksoftware.integration.hub.detect.detectables;

import java.io.File;
import java.util.stream.Collectors;

import com.blackducksoftware.integration.hub.detect.detector.Detector;
import com.blackducksoftware.integration.hub.detect.detector.DetectorEnvironment;
import com.blackducksoftware.integration.hub.detect.detector.DetectorException;
import com.blackducksoftware.integration.hub.detect.detector.DetectorType;
import com.blackducksoftware.integration.hub.detect.detector.ExtractionId;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocationType;
import com.blackducksoftware.integration.hub.detect.workflow.extraction.Extraction;
import com.blackducksoftware.integration.hub.detect.workflow.search.result.DetectorResult;
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
    public Extraction extract(final ExtractionId extractionId) {//TODO: FIX THIS PLEASE OH GOD
        File outputDirectory = new File("");
        ExtractionEnvironment extractionEnvironment = new ExtractionEnvironment(outputDirectory);
        com.synopsys.integration.detectable.Extraction detectableExtraction = detectable.extract(extractionEnvironment);

        Extraction.Builder extractionBuilder = new Extraction.Builder();
        extractionBuilder.codeLocations(detectableExtraction.codeLocations.stream()
                                            .map(it -> new DetectCodeLocation.Builder(DetectCodeLocationType.BITBAKE, it.getSourcePath(), it.getExternalId(), it.getDependencyGraph()).build())
                                            .collect(Collectors.toList()));
        extractionBuilder.success();
        return extractionBuilder.build();
    }
}
