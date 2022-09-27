package com.synopsys.integration.detect.lifecycle.boot.product;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;

public class BlackDuckVersionChecker {
    private final BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks;

    public BlackDuckVersionChecker(BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks) {
        this.blackDuckMinimumVersionChecks = blackDuckMinimumVersionChecks;
    }

    public boolean check(DetectPropertyConfiguration config, BlackDuckVersion actualBlackDuckVersion) {
        for (BlackDuckMinimumVersionCheck blackDuckMinimumVersionCheck : blackDuckMinimumVersionChecks.create()) {
            if (blackDuckMinimumVersionCheck.getTest().test(config) && !actualBlackDuckVersion.isAtLeast(blackDuckMinimumVersionCheck.getMinimumBlackDuckVersion())) {
                return false;
            }
        }
        return true;
    }
}
