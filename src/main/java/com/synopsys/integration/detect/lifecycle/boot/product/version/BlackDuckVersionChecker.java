package com.synopsys.integration.detect.lifecycle.boot.product.version;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;

public class BlackDuckVersionChecker {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
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
                logger.warn("{} requires at least Black Duck version {}; the connected server is only {}",
                    blackDuckMinimumVersionCheck.getDescription(), blackDuckMinimumVersionCheck.getMinimumBlackDuckVersion(),
                    actualBlackDuckVersion.get()
                );
                return false;
            }
        }
        return true;
    }
}
