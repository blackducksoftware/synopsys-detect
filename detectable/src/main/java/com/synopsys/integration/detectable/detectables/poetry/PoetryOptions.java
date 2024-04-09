package com.synopsys.integration.detectable.detectables.poetry;

import java.util.List;

public class PoetryOptions {
    private final List<String> excludedGroups;

    public PoetryOptions(List<String> excludedGroups) {
        this.excludedGroups = excludedGroups;
    }

    public List<String> getExcludedGroups() {
        return excludedGroups;
    }
}
