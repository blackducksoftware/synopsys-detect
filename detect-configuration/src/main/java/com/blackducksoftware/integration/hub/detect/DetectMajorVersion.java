package com.blackducksoftware.integration.hub.detect;

public enum DetectMajorVersion {
    ONE(1),
    TWO(2),
    THREE(3),
    FOUR(4),
    FIVE(5),
    SIX(6),
    SEVEN(7);

    private int value = 0;

    DetectMajorVersion(final int value) {
        this.value = value;
    }

    public int getIntValue() {
        return value;
    }

    public String getDisplayValue() {
        return Integer.toString(value) + ".0.0";
    }
}
