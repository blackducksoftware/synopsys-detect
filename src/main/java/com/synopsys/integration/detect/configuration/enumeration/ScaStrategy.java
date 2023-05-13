package com.synopsys.integration.detect.configuration.enumeration;

public enum ScaStrategy {
    
    CENTRALIZED ("centralized"),
    DISTRIBUTED ("distributed"),
    HYBRID ("hybrid");

    private final String displayName;
    
    ScaStrategy(String name) {
        this.displayName = name;
    }
    
    public String displayName() {
        return this.displayName;
    }
}
