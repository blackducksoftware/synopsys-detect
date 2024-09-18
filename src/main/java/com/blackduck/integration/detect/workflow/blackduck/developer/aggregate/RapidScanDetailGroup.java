package com.blackduck.integration.detect.workflow.blackduck.developer.aggregate;

public enum RapidScanDetailGroup {
    LICENSE("License"),
    POLICY("Components"),
    SECURITY("Security"),
    TRANSITIVE("Upgrade Guidance For Transitive Components"),
    VIOLATING_POLICIES("Other");

    private final String displayName;

    RapidScanDetailGroup(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
