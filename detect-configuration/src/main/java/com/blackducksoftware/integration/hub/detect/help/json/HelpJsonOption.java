package com.blackducksoftware.integration.hub.detect.help.json;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonOption {
    public String propertyName = "";
    public String propertyKey = "";
    public String propertyType = "";
    public String defaultValue = "";
    public String addedInVersion = "";
    public String group = "";
    public List<String> additionalGroups = new ArrayList<>();
    public String description = "";
    public String detailedDescription = "";
    public boolean deprecated = false;
    public String deprecatedDescription = "";
    public String deprecatedFailInVersion = "";
    public String deprecatedRemoveInVersion = "";
    public boolean strictValues = false;
    public boolean caseSensitiveValues = false;
    public boolean hasAcceptableValues = false;
    public List<String> acceptableValues = new ArrayList<>();

}
