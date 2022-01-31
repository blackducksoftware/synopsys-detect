package com.synopsys.integration.configuration.property;

//data class PropertyHelpInfo(val short: String, val long: String?)
public class PropertyHelpInfo {
    private final String shortText;
    private final String longText;

    public PropertyHelpInfo(String shortText, String longText) {
        this.shortText = shortText;
        this.longText = longText;
    }

    public String getShortText() {
        return shortText;
    }

    public String getLongText() {
        return longText;
    }
}
