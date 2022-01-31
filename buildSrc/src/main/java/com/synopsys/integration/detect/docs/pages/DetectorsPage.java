package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.Detector;

public class DetectorsPage {
    private final List<Detector> buildless;
    private final List<Detector> build;

    public DetectorsPage(List<Detector> buildless, List<Detector> build) {
        this.buildless = buildless;
        this.build = build;
    }

    public List<Detector> getBuildless() {
        return buildless;
    }

    public List<Detector> getBuild() {
        return build;
    }
}
