package com.blackducksoftware.integration.hub.detect.bomtool.search;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableManager;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner;

import java.io.File;

public class YarnBomToolSearcher extends PartialBomToolSearcher<BomToolSearchResult> {
    public YarnBomToolSearcher(final ExecutableManager executableManager, final ExecutableRunner executableRunner, final DetectFileManager detectFileManager, final DetectConfiguration detectConfiguration) {
        super(executableManager, executableRunner, detectFileManager, detectConfiguration);
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
