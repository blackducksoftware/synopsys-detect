package com.synopsys.integration.detect.configuration.enumeration;

public enum BlackduckScanMode {
    RAPID ("Rapid"),
    EPHEMERAL ("Ephemeral"),
    INTELLIGENT ("Intelligent");

    private final String lcName;
    BlackduckScanMode(String name) {
        this.lcName = name;
    }
    
    public String displayName() {
        return this.lcName;
    }
}
