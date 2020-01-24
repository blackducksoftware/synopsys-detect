package com.synopsys.integration.configuration.config

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

open class KeyUtilsTests {
    @Test
    fun replacesUnderscores() {
        Assertions.assertEquals("key.dot.dot", KeyUtils.normalizeKey("key_dot_dot"))
    }

    @Test
    fun replacesCapitals() {
        Assertions.assertEquals("key.lower", KeyUtils.normalizeKey("KEY.LOWER"))
    }

    @Test
    fun normalizesEnvKey() {
        Assertions.assertEquals("env.key", KeyUtils.normalizeKey("ENV_KEY"))
    }
}