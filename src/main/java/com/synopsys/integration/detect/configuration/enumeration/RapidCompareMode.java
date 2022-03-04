package com.synopsys.integration.detect.configuration.enumeration;

public enum RapidCompareMode {
    ALL("ALL"),
    BOM_COMPARE("BOM_COMPARE"),
    BOM_COMPARE_STRICT("BOM_COMPARE_STRICT");

    private final String headerValue;

    RapidCompareMode(String headerValue) {
        this.headerValue = headerValue;
    }

    public String getHeaderValue() {
        return headerValue;
    }

    public static final String HEADER_NAME = "X-BD-RAPID-SCAN-MODE";
}
