package com.synopsys.integration.detect.configuration.help.json.model;

public class HelpJsonDetectable {
    private String detectableName = "";
    private String detectableLanguage = "";
    private String detectableRequirementsMarkdown = "";
    private String detectableForge = "";
    private String detectableAccuracy = "";

    public String getDetectableName() {
        return detectableName;
    }

    public void setDetectableName(String detectableName) {
        this.detectableName = detectableName;
    }

    public String getDetectableLanguage() {
        return detectableLanguage;
    }

    public void setDetectableLanguage(String detectableLanguage) {
        this.detectableLanguage = detectableLanguage;
    }

    public String getDetectableRequirementsMarkdown() {
        return detectableRequirementsMarkdown;
    }

    public void setDetectableRequirementsMarkdown(String detectableRequirementsMarkdown) {
        this.detectableRequirementsMarkdown = detectableRequirementsMarkdown;
    }

    public String getDetectableForge() {
        return detectableForge;
    }

    public void setDetectableForge(String detectableForge) {
        this.detectableForge = detectableForge;
    }


    public String getDetectableAccuracy() {
        return detectableAccuracy;
    }

    public void setDetectableAccuracy(final String detectableAccuracy) {
        this.detectableAccuracy = detectableAccuracy;
    }
}
