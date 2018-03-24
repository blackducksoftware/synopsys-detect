package com.blackducksoftware.integration.hub.detect.bomtool;

import java.io.File;

public class BomToolSearchResult {
    private boolean applicable;

    public static final BomToolSearchResult BOM_TOOL_APPLIES = new BomToolSearchResult(true);
    public static final BomToolSearchResult BOM_TOOL_DOES_NOT_APPLY = new BomToolSearchResult(false);

    public BomToolSearchResult(boolean applicable) {
        this.applicable = applicable;
    }

    public boolean isApplicable() {
        return applicable;
    }

}
