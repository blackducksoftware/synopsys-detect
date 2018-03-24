package com.blackducksoftware.integration.hub.detect.bomtool.yarn;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolSearchResult;
import com.blackducksoftware.integration.hub.detect.bomtool.PartialBomToolSearcher;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;

import java.io.File;

public class YarnBomToolSearcher extends PartialBomToolSearcher<BomToolSearchResult> {
    private final DetectFileManager detectFileManager;

    public YarnBomToolSearcher(final DetectFileManager detectFileManager) {
        this.detectFileManager = detectFileManager;
    }

    @Override
    public BomToolSearchResult getSearchResult(final File directoryToSearch) {
        final boolean yarnApplies = detectFileManager.containsAllFiles(directoryToSearch, "yarn.lock");
        if (yarnApplies) {
            return BomToolSearchResult.BOM_TOOL_APPLIES;
        } else {
            return BomToolSearchResult.BOM_TOOL_DOES_NOT_APPLY;
        }
    }

}
