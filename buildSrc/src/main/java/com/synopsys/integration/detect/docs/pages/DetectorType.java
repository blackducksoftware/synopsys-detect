package com.synopsys.integration.detect.docs.pages;

import java.util.List;

public class DetectorType {
    private final String name;
    private final List<DetectorEntryPoint> entryPoints;

    public DetectorType(String name, List<DetectorEntryPoint> entryPoints) {
        this.name = name;
        this.entryPoints = entryPoints;
    }

    public String getName() {
        return name;
    }

    public List<DetectorEntryPoint> getEntryPoints() {
        return entryPoints;
    }
}
