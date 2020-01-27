package com.synopsys.integration.configuration.property.types.string

import org.apache.commons.lang3.RandomStringUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

class StringValueParserTest {
    @Test
    fun parseValid() {
        Assertions.assertEquals("any string should work", StringValueParser().parse("any string should work"))
    }

    @RepeatedTest(10)
    fun parseRandom() {
        Assertions.assertDoesNotThrow {
            val randomString = RandomStringUtils.random(20, true, true)
            println("Testing with random string '$randomString'")
            StringValueParser().parse(randomString)
        }
    }
}