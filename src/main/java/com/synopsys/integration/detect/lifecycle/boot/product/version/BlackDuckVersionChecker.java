package com.synopsys.integration.detect.lifecycle.boot.product.version;

import java.util.Optional;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;

public class BlackDuckVersionChecker {
    private final BlackDuckVersionParser parser;
    private final BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks;
    private final DetectPropertyConfiguration detectPropertyConfiguration;

    public BlackDuckVersionChecker(
        BlackDuckVersionParser parser,
        BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks,
        DetectPropertyConfiguration detectPropertyConfiguration
    ) {
        this.parser = parser;
        this.blackDuckMinimumVersionChecks = blackDuckMinimumVersionChecks;
        this.detectPropertyConfiguration = detectPropertyConfiguration;
    }

    public boolean check(String actualBlackDuckVersionString) {
        Optional<BlackDuckVersion> actualBlackDuckVersion = parser.parse(actualBlackDuckVersionString);
        if (!actualBlackDuckVersion.isPresent()) {
            return true;
        }
        for (BlackDuckMinimumVersionCheck blackDuckMinimumVersionCheck : blackDuckMinimumVersionChecks.get()) {
            if (blackDuckMinimumVersionCheck.getTest().test(detectPropertyConfiguration) && !actualBlackDuckVersion.get()
                .isAtLeast(blackDuckMinimumVersionCheck.getMinimumBlackDuckVersion())) {
                return false;
            }
        }
        return true;
    }
}
