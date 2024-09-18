package com.blackduck.integration.configuration.property.base;

import static java.util.Collections.emptyList;

import java.util.List;

import com.blackduck.integration.configuration.parse.ListValueParser;
import com.blackduck.integration.configuration.parse.ValueParser;
import com.blackduck.integration.configuration.property.Property;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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