package com.synopsys.integration.detect.workflow.componentlocationanalysis;

/**
 * The ComponentLocatorException is needed/has the added ebnefit of ... compared to existing exception classes. in the future we may enhance this with specific error codes
 * The need came from wanting to report failure status in status.json file. in the future this could be improved to give more detail and what not.
 * can echo an exception in the future
 */
public class ComponentLocatorException extends Exception { // should it extend operationexception? or integration exception?
    private static final long serialVersionUID = 1L;

    public ComponentLocatorException(Exception exception) {
        super(exception);
    }

    public ComponentLocatorException(String message) {
        super(message);
    }

}
