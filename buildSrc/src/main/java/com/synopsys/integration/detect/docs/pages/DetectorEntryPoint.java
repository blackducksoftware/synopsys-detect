package com.synopsys.integration.detect.docs.pages;

import java.util.List;

import com.synopsys.integration.detect.docs.model.Detectable;

public class DetectorEntryPoint {
    private final String name;
    private final List<Detectable> detectables;

    public DetectorEntryPoint(String name, List<Detectable> detectables) {
        this.name = name;
        this.detectables = detectables;
    }

    public String getName() {
        return name;
    }

    public List<Detectable> getDetectables() {
        return detectables;
    }
}
