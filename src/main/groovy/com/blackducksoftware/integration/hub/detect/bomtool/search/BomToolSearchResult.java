package com.blackducksoftware.integration.hub.detect.bomtool.search;

public class BomToolSearchResult {
    public static final BomToolSearchResult BOM_TOOL_APPLIES = new BomToolSearchResult(true);
    public static final BomToolSearchResult BOM_TOOL_DOES_NOT_APPLY = new BomToolSearchResult(false);

    private final boolean applicable;

    public BomToolSearchResult(final boolean applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return applicable;
    }

}
