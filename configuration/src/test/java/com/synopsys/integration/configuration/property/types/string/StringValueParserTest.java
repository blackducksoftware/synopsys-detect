package com.synopsys.integration.configuration.property.types.string;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

public class StringValueParserTest {
    @Test
    public void parseValid() {
        Assertions.assertEquals("any string should work", new StringValueParser().parse("any string should work"));
    }

    @RepeatedTest(10)
    public void parseRandom() {
        Assertions.assertDoesNotThrow(() -> {
            String randomString = RandomStringUtils.random(20, true, true);
            System.out.println("Testing with random string '$randomString'");
            new StringValueParser().parse(randomString);
        });
    }
}