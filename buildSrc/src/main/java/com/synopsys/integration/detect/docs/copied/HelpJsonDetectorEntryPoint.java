package com.synopsys.integration.detect.docs.copied;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonDetectorEntryPoint {
    private String name;
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
