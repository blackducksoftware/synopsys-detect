package com.synopsys.integration.detect.docs.pages;

import java.util.List;

public class DetectorCascadePage {
    private final List<DetectorEntryPoint> entryPoints;

    public DetectorCascadePage(List<DetectorEntryPoint> entryPoints) {this.entryPoints = entryPoints;}

    public List<DetectorEntryPoint> getEntryPoints() {
        return entryPoints;
    }

}
