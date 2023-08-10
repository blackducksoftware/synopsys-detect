package com.synopsys.integration.detect.configuration.enumeration;

public enum BlackduckScanMode {
    RAPID ("Rapid"),
    STATELESS ("Stateless"),
    INTELLIGENT ("Intelligent");

    private final String displayName;
    BlackduckScanMode(String name) {
        this.displayName = name;
    }
    
    public String displayName() {
        return this.displayName;
    }
}
