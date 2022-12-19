package com.synopsys.integration.detector.rule;

import com.synopsys.integration.detectable.detectable.DetectableAccuracyType;
import com.synopsys.integration.detector.base.DetectableCreatable;

public class DetectableDefinition {
    //TODO Could add an id field that is set from .getClassName(),
    // right now name is used as an ID for applied in parent. OR could make this hashable or something.
    private final DetectableCreatable detectableCreatable;
    private final String name;
    private final String forge;
    private final String language;
    private final String requirementsMarkdown;
    private final DetectableAccuracyType accuracyType;

    public DetectableDefinition(
        DetectableCreatable detectableCreatable,
        String name,
        String forge,
        String language,
        String requirementsMarkdown,
        DetectableAccuracyType accuracyType
    ) {
        this.detectableCreatable = detectableCreatable;
        this.name = name;
        this.forge = forge;
        this.language = language;
        this.requirementsMarkdown = requirementsMarkdown;
        this.accuracyType = accuracyType;
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

    public DetectableAccuracyType getAccuracyType() {
        return accuracyType;
    }

    @Override
    public String toString() {
        return getName();
    }
}
