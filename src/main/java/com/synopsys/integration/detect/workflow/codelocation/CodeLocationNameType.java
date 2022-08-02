package com.synopsys.integration.detect.workflow.codelocation;

public enum CodeLocationNameType {
    BOM("Black Duck I/O Export"),
    DOCKER("docker"),
    IMPACT_ANALYSIS("impact_analysis"),
    SCAN("scan"),
    IAC("iac");

    private final String name;

    CodeLocationNameType(String name) {
        this.name = name;
    }

    String getName() {
        return this.name;
    }
}
