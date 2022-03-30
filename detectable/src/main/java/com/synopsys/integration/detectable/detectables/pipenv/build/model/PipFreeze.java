package com.synopsys.integration.detectable.detectables.pipenv.build.model;

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
