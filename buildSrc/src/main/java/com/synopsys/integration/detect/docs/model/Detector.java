/*
 * buildSrc
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.docs.model;

import com.synopsys.integration.detect.docs.copied.HelpJsonDetector;

public class Detector {
    private final String detectorType;
    private final String detectorName;
    private final String detectableLanguage;
    private final String detectableForge;
    private final String detectableRequirementsMarkdown;

    public Detector(final HelpJsonDetector detector) {
        this(detector.getDetectorType(), detector.getDetectorName(), detector.getDetectableLanguage(), detector.getDetectableForge(), detector.getDetectableRequirementsMarkdown());
    }

    public Detector(final String detectorType, final String detectorName, final String detectableLanguage, final String detectableForge, final String detectableRequirementsMarkdown) {
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
