package com.synopsys.integration.detectable.detectables.yarn.parse.entry;

import com.synopsys.integration.util.Stringable;

public class YarnLockEntryId extends Stringable {
    private final String name;
    private final String version;

    public YarnLockEntryId(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
