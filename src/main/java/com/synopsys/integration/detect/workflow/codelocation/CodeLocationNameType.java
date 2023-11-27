package com.synopsys.integration.detect.workflow.codelocation;

public enum CodeLocationNameType {
    BOM("bdio"),
    IMPACT_ANALYSIS("impact"),
    SIGNATURE("signature"),
    BINARY("binary"),
    CONTAINER("container"),
    IAC("iac"),
    REVERSING_LABS("ReversingLabs");

    private final String name;

    CodeLocationNameType(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
}
