package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import java.util.List;

public class ComponentLocatorLibInput {
    private final String sourcePath;
    private final CLLMetadata globalMetadata;
    private final List<CLLComponent> componentList;

    public ComponentLocatorLibInput(String sourcePath, CLLMetadata globalMetadata, List<CLLComponent> componentList) {
        this.sourcePath = sourcePath;
        this.globalMetadata = globalMetadata;
        this.componentList = componentList;
    }

}
