package com.blackduck.integration.detect.lifecycle.run.operation;

import java.util.Optional;

import com.blackduck.integration.blackduck.version.BlackDuckVersion;
import com.blackduck.integration.detect.lifecycle.run.data.BlackDuckRunData;
import com.blackduck.integration.detect.lifecycle.run.data.ScanCreationResponse;

public class ScassOperationRunner {
    
    private final BlackDuckRunData blackDuckRunData;
    
    private static final BlackDuckVersion MIN_SCASS_SCAN_VERSION = new BlackDuckVersion(2025, 1, 0);

    public ScassOperationRunner(BlackDuckRunData blackDuckRunData) {
        this.blackDuckRunData = blackDuckRunData;
    }
    
    public boolean areScassScansPossible() {
        Optional<BlackDuckVersion> blackDuckVersion = blackDuckRunData.getBlackDuckServerVersion();
        return blackDuckVersion.isPresent() && blackDuckVersion.get().isAtLeast(MIN_SCASS_SCAN_VERSION);
    }
    
    public ScanCreationResponse initiateScassScan() {
        // TODO refactor call
        return null;
    }
}
