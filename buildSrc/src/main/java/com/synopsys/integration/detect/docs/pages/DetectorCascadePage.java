package com.synopsys.integration.detect.docs.pages;

import java.util.List;

public class DetectorCascadePage {
    private final List<DetectorType> detectorTypes;

    public DetectorCascadePage(List<DetectorType> detectorTypes) {this.detectorTypes = detectorTypes;}

    public List<DetectorType> getDetectorTypes() {
        return detectorTypes;
    }

}
