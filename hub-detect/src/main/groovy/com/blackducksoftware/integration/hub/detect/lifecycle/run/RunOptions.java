package com.blackducksoftware.integration.hub.detect.lifecycle.run;

public class RunOptions {
    private final boolean bomToolsEnabled;
    private final boolean sigScanEnabled;
    private final boolean binScanEnabled;
    private final boolean isOnline;
    private final boolean unmapCodeLocations;
    private final boolean swipEnabled;
    private final String aggregateName;
    private final String preferredTools;

    public RunOptions(final boolean bomToolsEnabled, final boolean sigScanEnabled, final boolean binScanEnabled, final boolean isOnline, final boolean unmapCodeLocations, final boolean swipEnabled, final String aggregateName,
        final String preferredTools) {
        this.bomToolsEnabled = bomToolsEnabled;
        this.sigScanEnabled = sigScanEnabled;
        this.binScanEnabled = binScanEnabled;
        this.isOnline = isOnline;
        this.unmapCodeLocations = unmapCodeLocations;
        this.swipEnabled = swipEnabled;
        this.aggregateName = aggregateName;
        this.preferredTools = preferredTools;
    }

    public boolean isBomToolsEnabled() {
        return bomToolsEnabled;
    }

    public boolean isSigScanEnabled() {
        return sigScanEnabled;
    }

    public boolean isBinScanEnabled() {
        return binScanEnabled;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isUnmapCodeLocations() {
        return unmapCodeLocations;
    }

    public boolean isSwipEnabled() {
        return swipEnabled;
    }

    public String getAggregateName() {
        return aggregateName;
    }

    public String getPreferredTools() {
        return preferredTools;
    }
}
