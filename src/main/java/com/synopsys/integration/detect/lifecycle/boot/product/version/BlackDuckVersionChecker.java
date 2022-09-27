package com.synopsys.integration.detect.lifecycle.boot.product.version;

import java.util.Optional;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;

public class BlackDuckVersionChecker {
    private final BlackDuckVersionParser parser;
    private final BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks;

    public BlackDuckVersionChecker(BlackDuckVersionParser parser, BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks) {
        this.parser = parser;
        this.blackDuckMinimumVersionChecks = blackDuckMinimumVersionChecks;
    }

    public boolean check(DetectPropertyConfiguration config, String actualBlackDuckVersionString) {
        Optional<BlackDuckVersion> actualBlackDuckVersion = parser.parse(actualBlackDuckVersionString);
        if (!actualBlackDuckVersion.isPresent()) {
            return true;
        }
        for (BlackDuckMinimumVersionCheck blackDuckMinimumVersionCheck : blackDuckMinimumVersionChecks.get()) {
            if (blackDuckMinimumVersionCheck.getTest().test(config) && !actualBlackDuckVersion.get().isAtLeast(blackDuckMinimumVersionCheck.getMinimumBlackDuckVersion())) {
                return false;
            }
        }
        return true;
    }
}
