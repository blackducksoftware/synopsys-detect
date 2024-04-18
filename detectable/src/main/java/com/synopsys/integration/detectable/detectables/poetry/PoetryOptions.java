package com.synopsys.integration.detectable.detectables.poetry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PoetryOptions {
    private final Set<String> excludedGroups;

    public PoetryOptions(List<String> excludedGroups) {
        this.excludedGroups = new HashSet<>(excludedGroups);
    }

    public Set<String> getExcludedGroups() {
        return excludedGroups;
    }
}
