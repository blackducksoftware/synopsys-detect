package com.blackduck.integration.configuration.config;

import com.blackduck.integration.configuration.util.KeyUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class KeyUtilsTests {
    @Test
    public void replacesUnderscores() {
        Assertions.assertEquals("key.dot.dot", KeyUtils.normalizeKey("key_dot_dot"));
    }

    @Test
    public void replacesCapitals() {
        Assertions.assertEquals("key.lower", KeyUtils.normalizeKey("KEY.LOWER"));
    }

    @Test
    public void normalizesEnvKey() {
        Assertions.assertEquals("env.key", KeyUtils.normalizeKey("ENV_KEY"));
    }
}