package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonDetectorRule {
    private String detectorType = "";
    private List<HelpJsonDetectorEntryPoint> entryPoints = new ArrayList<>();

    public List<HelpJsonDetectorEntryPoint> getEntryPoints() {
        return entryPoints;
    }

    public void setEntryPoints(List<HelpJsonDetectorEntryPoint> entryPoints) {
        this.entryPoints = entryPoints;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(String detectorType) {
        this.detectorType = detectorType;
    }
}