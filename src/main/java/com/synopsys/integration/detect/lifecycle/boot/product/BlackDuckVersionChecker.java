package com.synopsys.integration.detect.lifecycle.boot.product;

import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;

public class BlackDuckVersionChecker {
    private final BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks;

    public BlackDuckVersionChecker(BlackDuckMinimumVersionChecks blackDuckMinimumVersionChecks) {
        this.blackDuckMinimumVersionChecks = blackDuckMinimumVersionChecks;
    }

    public boolean check(DetectPropertyConfiguration config, BlackDuckVersion actualBlackDuckVersion) {
        for (Pair<Predicate<DetectPropertyConfiguration>, BlackDuckVersion> check : blackDuckMinimumVersionChecks.create()) {
            if (check.getLeft().test(config)) {
                if (!actualBlackDuckVersion.isAtLeast(check.getRight())) {
                    return false;
                }
            }
        }
        return true;
    }
}
