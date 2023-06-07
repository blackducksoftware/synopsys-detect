package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import java.util.List;

/**
 * This class adheres to Component Locator Library's input schema.
 * Any changes made here to the expected input should be accompanied by changes in the library and vice versa.
 */
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
