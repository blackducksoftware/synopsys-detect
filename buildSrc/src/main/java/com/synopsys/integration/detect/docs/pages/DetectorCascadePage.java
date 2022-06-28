package com.synopsys.integration.detect.docs.pages;

import java.util.List;

public class DetectorCascadePage {
    private final List<DetectorEntryPoint> detectors;

    public DetectorCascadePage(List<DetectorEntryPoint> detectors) {this.detectors = detectors;}

    public List<DetectorEntryPoint> getDetectors() {
        return detectors;
    }

}
