package com.synopsys.integration.detect.workflow.blackduck.report;

public class PolicyRule {
    private final String name;
    private final String description;

    public PolicyRule(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

}
