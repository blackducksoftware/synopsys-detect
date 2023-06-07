package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.util.List;
import java.util.Objects;

/**
 * This class adheres to Component Locator Library's input schema.
 * Any changes made here to the expected input should be accompanied by changes in the library and vice versa.
 *
 * Will be fully implemented in a subsequent pull request to the Fix PR feature branch.
 */
public class ComponentLocatorInput {
    private final String sourcePath;
    private final Metadata globalMetadata;
    private final List<Component> componentList;

    public ComponentLocatorInput(String sourcePath, Metadata globalMetadata, List<Component> componentList) {
        this.sourcePath = sourcePath;
        this.globalMetadata = globalMetadata; //Objects.requireNonNullElse(globalMetadata, new CLLMetadata());
        this.componentList = componentList;
    }
}
