package com.synopsys.integration.detect.workflow.componentlocationanalysis;

/**
 * The ComponentLocatorException is needed to appropriately set and report the status of Component Locator related operations while letting Detect exit successfully in all cases.
 */
public class ComponentLocatorException extends Exception {
    private static final long serialVersionUID = 1L;
    public ComponentLocatorException(String message) {
        super(message);
    }

}
