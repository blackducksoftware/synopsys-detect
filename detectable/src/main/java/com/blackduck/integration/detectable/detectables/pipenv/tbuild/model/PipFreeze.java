package com.blackduck.integration.detectable.detectables.pipenv.tbuild.model;

import java.util.List;

public class PipFreeze {
    private final List<PipFreezeEntry> entries;

    public PipFreeze(List<PipFreezeEntry> entries) {
        this.entries = entries;
    }

    public List<PipFreezeEntry> getEntries() {
        return entries;
    }
}
