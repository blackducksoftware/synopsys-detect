package com.synopsys.integration.detect.lifecycle.boot.product;

import java.util.function.Predicate;

import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;

public class BlackDuckMinimumVersionCheck {
    private final String description;
    private final Predicate<DetectPropertyConfiguration> test;
    private final BlackDuckVersion minimumBlackDuckVersion;

    public BlackDuckMinimumVersionCheck(String description, Predicate<DetectPropertyConfiguration> test, BlackDuckVersion minimumBlackDuckVersion) {
        this.description = description;
        this.test = test;
        this.minimumBlackDuckVersion = minimumBlackDuckVersion;
    }

    public String getDescription() {
        return description;
    }

    public Predicate<DetectPropertyConfiguration> getTest() {
        return test;
    }

    public BlackDuckVersion getMinimumBlackDuckVersion() {
        return minimumBlackDuckVersion;
    }
}
