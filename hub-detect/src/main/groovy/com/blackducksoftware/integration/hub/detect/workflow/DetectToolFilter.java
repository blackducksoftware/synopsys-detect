package com.blackducksoftware.integration.hub.detect.workflow;

import java.util.Optional;

import com.blackducksoftware.integration.hub.detect.DetectTool;
import com.synopsys.integration.util.ExcludedIncludedFilter;

public class DetectToolFilter {

    private final ExcludedIncludedFilter excludedIncludedFilter;
    private final Optional<Boolean> deprecatedSigScanDisabled;
    private final Optional<Boolean> deprecatedSwipEnabled;

    public DetectToolFilter(String includedTools, String excludedTools, Optional<Boolean> deprecatedSigScanDisabled, Optional<Boolean> deprecatedSwipEnabled) {
        this.excludedIncludedFilter = new ExcludedIncludedFilter(includedTools, excludedTools);

        this.deprecatedSigScanDisabled = deprecatedSigScanDisabled;
        this.deprecatedSwipEnabled = deprecatedSwipEnabled;
    }

    public boolean shouldInclude(DetectTool detectTool) {
        if (detectTool == DetectTool.SIGNATURE_SCAN && deprecatedSigScanDisabled.isPresent()){
            return !deprecatedSigScanDisabled.get();
        }else if (detectTool == DetectTool.SWIP_CLI && deprecatedSwipEnabled.isPresent()){
            return  deprecatedSwipEnabled.get();
        }

        return excludedIncludedFilter.shouldInclude(detectTool.name());
    }
}
