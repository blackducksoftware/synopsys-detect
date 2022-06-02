package com.synopsys.integration.detect.docs.model;

public class Detectable {
    private final String name;
    private final String language;
    private final String forge;
    private final String requirementsMarkdown;

    public Detectable(
        String name,
        String language,
        String forge,
        String requirementsMarkdown
    ) {
        this.name = name;
        this.language = language;
        this.forge = forge;
        this.requirementsMarkdown = requirementsMarkdown;
    }

    public String getName() {
        return name;
    }

    public String getLanguage() {
        return language;
    }

    public String getForge() {
        return forge;
    }

    public String getRequirementsMarkdown() {
        return requirementsMarkdown;
    }
}
