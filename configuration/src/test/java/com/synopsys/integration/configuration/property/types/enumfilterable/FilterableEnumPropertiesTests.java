package com.synopsys.integration.configuration.property.types.enumfilterable;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class FilterableEnumPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableFilterableEnumProperty<Example> property = new NullableFilterableEnumProperty<>("enum.nullable", Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "NONE"));

        FilterableEnumValue<Example> value = config.getValue(property).get();

        if (value.isAll()) {
            fail("Expected type to be None instead of All.");
        } else if (value.getValue().isPresent()) {
            fail("Expected type to be None instead of Value: ${value.value.get()}");
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE", "ALL"));
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        FilterableEnumProperty<Example> property = new FilterableEnumProperty<>("enum.valued", FilterableEnumValue.allValue(), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));

        FilterableEnumValue<Example> value = config.getValue(property);

        if (value.isNone()) {
            fail("Expected type to be Value instead of None.");
        } else if (value.isAll()) {
            fail("Expected type to be Value instead of All.");
        } else {
            Assertions.assertEquals(FilterableEnumValue.value(Example.THIRD), value);
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE", "ALL"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        FilterableEnumListProperty<Example> property = new FilterableEnumListProperty<>("enum.list", Arrays.asList(FilterableEnumValue.value(Example.ANOTHER), FilterableEnumValue.value(Example.THING)), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "ANOTHER,THING"));

        List<FilterableEnumValue<Example>> value = config.getValue(property).toFilterableValues();

        if (FilterableEnumUtils.containsNone(value)) {
            fail("Expected type to be Value instead of None.");
        } else if (FilterableEnumUtils.containsAll(value)) {
            fail("Expected type to be Value instead of All.");
        } else {
            Assertions.assertEquals(Arrays.asList(FilterableEnumValue.value(Example.ANOTHER), FilterableEnumValue.value(Example.THING)), value);
        }

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE", "ALL"));
    }
}