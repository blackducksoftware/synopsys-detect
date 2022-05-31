package com.synopsys.integration.detector.rule;

import com.synopsys.integration.detector.base.DetectableCreatable;

public class DetectableDefinition {
    private final DetectableCreatable detectableCreatable;
    private final String name;
    private final String forge;
    private final String language;
    private final String requirementsMarkdown;

    public DetectableDefinition(DetectableCreatable detectableCreatable, String name, String forge, String language, String requirementsMarkdown) {
        this.detectableCreatable = detectableCreatable;
        this.name = name;
        this.forge = forge;
        this.language = language;
        this.requirementsMarkdown = requirementsMarkdown;
    }

    public DetectableCreatable getDetectableCreatable() {
        return detectableCreatable;
    }

    public String getName() {
        return name;
    }

    public String getForge() {
        return forge;
    }

    public String getLanguage() {
        return language;
    }

    public String getRequirementsMarkdown() {
        return requirementsMarkdown;
    }
}
