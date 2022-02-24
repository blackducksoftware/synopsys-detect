package com.synopsys.integration.configuration.property.base;

import static java.util.Collections.emptyList;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.parse.ListValueParser;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.Property;

public class ValuedListPropertyTest {
    private static class TestValueParser extends ValueParser<String> {
        @NotNull
        @Override
        public String parse(@NotNull String value) {
            return value;
        }
    }

    private static class TestListProperty extends ValuedAlikeListProperty<String> {
        public TestListProperty(@NotNull String key, List<String> defaultValue) {
            super(key, new ListValueParser<>(new TestValueParser()), defaultValue);
        }
    }

    @Test
    public void describeDefault() {
        Property testProperty = new TestListProperty("key", emptyList());
        Assertions.assertNotNull(testProperty.describeDefault(), "The default value description should be set.");
    }

    @Test
    public void isCommaSeparated() {
        Property testProperty = new TestListProperty("key", emptyList());
        Assertions.assertTrue(testProperty.isCommaSeparated(), "This parser should separate on comma and should report that it does.");
    }
}