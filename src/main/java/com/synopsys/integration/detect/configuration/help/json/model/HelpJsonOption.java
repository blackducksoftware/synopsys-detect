package com.synopsys.integration.detect.configuration.help.json.model;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.Nullable;

public class HelpJsonOption {
    private String propertyName = "";
    private String propertyKey = "";
    private String propertyType = "";
    @Nullable
    private String defaultValue = "";
    private String addedInVersion = "";
    private String category = "";
    private String group = "";
    @Nullable
    private String superGroup = "";
    private List<String> additionalGroups = new ArrayList<>();
    private String description = "";
    private String detailedDescription = "";
    private Boolean deprecated = false;
    private String deprecatedDescription = "";
    private String deprecatedRemoveInVersion = "";
    private Boolean strictValues = false;
    private Boolean caseSensitiveValues = false;
    private Boolean hasAcceptableValues = false;
    private Boolean isCommaSeparatedList = false;
    private List<String> acceptableValues = new ArrayList<>();
    private List<HelpJsonOptionDeprecatedValue> deprecatedValues = new ArrayList<>();
    private String example = "";

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyKey() {
        return propertyKey;
    }

    public void setPropertyKey(String propertyKey) {
        this.propertyKey = propertyKey;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getAddedInVersion() {
        return addedInVersion;
    }

    public void setAddedInVersion(String addedInVersion) {
        this.addedInVersion = addedInVersion;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSuperGroup() {
        return superGroup;
    }

    public void setSuperGroup(String superGroup) {
        this.superGroup = superGroup;
    }

    public List<String> getAdditionalGroups() {
        return additionalGroups;
    }

    public void setAdditionalGroups(List<String> additionalGroups) {
        this.additionalGroups = additionalGroups;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetailedDescription() {
        return detailedDescription;
    }

    public void setDetailedDescription(String detailedDescription) {
        this.detailedDescription = detailedDescription;
    }

    public Boolean getDeprecated() {
        return deprecated;
    }

    public void setDeprecated(Boolean deprecated) {
        this.deprecated = deprecated;
    }

    public String getDeprecatedDescription() {
        return deprecatedDescription;
    }

    public void setDeprecatedDescription(String deprecatedDescription) {
        this.deprecatedDescription = deprecatedDescription;
    }

    public String getDeprecatedRemoveInVersion() {
        return deprecatedRemoveInVersion;
    }

    public void setDeprecatedRemoveInVersion(String deprecatedRemoveInVersion) {
        this.deprecatedRemoveInVersion = deprecatedRemoveInVersion;
    }

    public Boolean getStrictValues() {
        return strictValues;
    }

    public void setStrictValues(Boolean strictValues) {
        this.strictValues = strictValues;
    }

    public Boolean getCaseSensitiveValues() {
        return caseSensitiveValues;
    }

    public void setCaseSensitiveValues(Boolean caseSensitiveValues) {
        this.caseSensitiveValues = caseSensitiveValues;
    }

    public Boolean getHasAcceptableValues() {
        return hasAcceptableValues;
    }

    public void setHasAcceptableValues(Boolean hasAcceptableValues) {
        this.hasAcceptableValues = hasAcceptableValues;
    }

    public Boolean getCommaSeparatedList() {
        return isCommaSeparatedList;
    }

    public void setCommaSeparatedList(Boolean commaSeparatedList) {
        isCommaSeparatedList = commaSeparatedList;
    }

    public List<String> getAcceptableValues() {
        return acceptableValues;
    }

    public void setAcceptableValues(List<String> acceptableValues) {
        this.acceptableValues = acceptableValues;
    }

    public String getExample() {
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }

    public List<HelpJsonOptionDeprecatedValue> getDeprecatedValues() {
        return deprecatedValues;
    }

    public void setDeprecatedValues(List<HelpJsonOptionDeprecatedValue> deprecatedValues) {
        this.deprecatedValues = deprecatedValues;
    }
}
