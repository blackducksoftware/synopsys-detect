package com.synopsys.integration.detectable.detectables.yarn;

public class YarnLockOptions {
    private final boolean useProductionOnly;

    public YarnLockOptions(final boolean useProductionOnly) {
        this.useProductionOnly = useProductionOnly;
    }

    public boolean useProductionOnly() {
        return useProductionOnly;
    }
}
