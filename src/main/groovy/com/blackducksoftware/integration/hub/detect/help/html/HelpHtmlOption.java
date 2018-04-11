package com.blackducksoftware.integration.hub.detect.help.html;

public class HelpHtmlOption {
    public String key;
    public String defaultValue;
    public String description;
    
    public HelpHtmlOption(String key, String defaultValue, String description) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.description = description;
    }
    
    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}