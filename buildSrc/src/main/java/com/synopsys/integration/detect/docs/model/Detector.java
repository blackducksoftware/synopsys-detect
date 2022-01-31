package com.synopsys.integration.detect.docs.model;

import com.synopsys.integration.detect.docs.copied.HelpJsonDetector;

public class Detector {
    private final String detectorType;
    private final String detectorName;
    private final String detectableLanguage;
    private final String detectableForge;
    private final String detectableRequirementsMarkdown;

    public Detector(HelpJsonDetector detector) {
        this(detector.getDetectorType(), detector.getDetectorName(), detector.getDetectableLanguage(), detector.getDetectableForge(), detector.getDetectableRequirementsMarkdown());
    }

    public Detector(String detectorType, String detectorName, String detectableLanguage, String detectableForge, String detectableRequirementsMarkdown) {
        this.detectorType = detectorType;
        this.detectorName = detectorName;
        this.detectableLanguage = detectableLanguage;
        this.detectableForge = detectableForge;
        this.detectableRequirementsMarkdown = detectableRequirementsMarkdown;
    }

    public String getDetectorType() {
        return detectorType;
    }

    public String getDetectorName() {
        return detectorName;
    }

    public String getDetectableLanguage() {
        return detectableLanguage;
    }

    public String getDetectableForge() {
        return detectableForge;
    }

    public String getDetectableRequirementsMarkdown() {
        return detectableRequirementsMarkdown;
    }
}
