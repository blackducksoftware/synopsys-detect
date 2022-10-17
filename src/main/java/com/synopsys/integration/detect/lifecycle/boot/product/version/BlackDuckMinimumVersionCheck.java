package com.synopsys.integration.detect.lifecycle.boot.product.version;

import java.util.function.Predicate;

public class BlackDuckMinimumVersionCheck {
    private final String description;
    private final Predicate<BlackDuckVersionSensitiveOptions> test;
    private final BlackDuckVersion minimumBlackDuckVersion;

    public BlackDuckMinimumVersionCheck(String description, Predicate<BlackDuckVersionSensitiveOptions> test, BlackDuckVersion minimumBlackDuckVersion) {
        this.description = description;
        this.test = test;
        this.minimumBlackDuckVersion = minimumBlackDuckVersion;
    }

    public String getDescription() {
        return description;
    }

    public Predicate<BlackDuckVersionSensitiveOptions> getTest() {
        return test;
    }

    public BlackDuckVersion getMinimumBlackDuckVersion() {
        return minimumBlackDuckVersion;
    }
}
