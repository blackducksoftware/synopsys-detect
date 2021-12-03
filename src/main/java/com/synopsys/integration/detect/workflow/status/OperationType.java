package com.synopsys.integration.detect.workflow.status;

public enum OperationType {
    PUBLIC, //A public operation is ALWAYS present in the Operation Status and Logs
    INTERNAL // An internal operation is only logged or visible if it fails, otherwise it is not shown to the user.
}
