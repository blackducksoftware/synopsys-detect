package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonDetectorEntryPoint {
    private String name; //TODO (detectors): May not need this.
    private List<HelpJsonDetectable> detectables = new ArrayList<>();

    public List<HelpJsonDetectable> getDetectables() {
        return detectables;
    }

    public void setDetectables(List<HelpJsonDetectable> detectables) {
        this.detectables = detectables;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
