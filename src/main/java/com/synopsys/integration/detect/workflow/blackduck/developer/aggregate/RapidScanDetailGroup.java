package com.synopsys.integration.detect.workflow.blackduck.developer.aggregate;

public enum RapidScanDetailGroup {
    LICENSE("License"),
    POLICY("Components"),
    SECURITY("Security"),
    TRANSITIVE("Upgrade Guidance For Transitive Components");

    private final String displayName;

    RapidScanDetailGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
