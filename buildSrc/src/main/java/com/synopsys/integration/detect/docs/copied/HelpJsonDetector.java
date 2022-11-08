package com.synopsys.integration.detect.docs.copied;

import java.util.ArrayList;
import java.util.List;

public class HelpJsonDetector {
    private String detectableLanguage = "";
    private String detectableRequirementsMarkdown = "";
    private String detectableForge = "";
    private String detectorType = "";
    private String detectorName = "";
    private String detectorDescriptiveName = "";
    private Integer maxDepth = 0;
    private Boolean nestable = false;
    private Boolean nestInvisible = false;

    private List<String> yieldsTo = new ArrayList<>();

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

    public String getDetectorType() {
        return detectorType;
    }

    public void setDetectorType(String detectorType) {
        this.detectorType = detectorType;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public void setDetectorName(String detectorName) {
        this.detectorName = detectorName;
    }

    public String getDetectorDescriptiveName() {
        return detectorDescriptiveName;
    }

    public void setDetectorDescriptiveName(String detectorDescriptiveName) {
        this.detectorDescriptiveName = detectorDescriptiveName;
    }

    public Integer getMaxDepth() {
        return maxDepth;
    }

    public void setMaxDepth(Integer maxDepth) {
        this.maxDepth = maxDepth;
    }

    public Boolean getNestable() {
        return nestable;
    }

    public void setNestable(Boolean nestable) {
        this.nestable = nestable;
    }

    public Boolean getNestInvisible() {
        return nestInvisible;
    }

    public void setNestInvisible(Boolean nestInvisible) {
        this.nestInvisible = nestInvisible;
    }

    public List<String> getYieldsTo() {
        return yieldsTo;
    }

    public void setYieldsTo(List<String> yieldsTo) {
        this.yieldsTo = yieldsTo;
    }

}

