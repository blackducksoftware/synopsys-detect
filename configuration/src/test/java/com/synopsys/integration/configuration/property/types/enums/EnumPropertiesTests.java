package com.synopsys.integration.configuration.property.types.enums;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static com.synopsys.integration.configuration.util.ConfigTestUtils.emptyConfig;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class EnumPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testExampleValues() {
        NullableEnumProperty<Example> property = new NullableEnumProperty<>("example.list", Example.class);
        Assertions.assertEquals(Bds.listOf("THING", "ANOTHER", "THIRD"), property.listExampleValues());
    }

    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableEnumProperty<Example> property = new NullableEnumProperty<>("enum.nullable", Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER"));
        Assertions.assertEquals(Optional.of(Example.ANOTHER), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        EnumProperty<Example> property = new EnumProperty<>("enum.valued", Example.THIRD, Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));
        Assertions.assertEquals(Example.THIRD, config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testDeprecatedValue() throws InvalidPropertyException {
        EnumProperty<Example> property = new EnumProperty<>("enum.valued", Example.ANOTHER, Example.class);
        property.deprecateValue(Example.THIRD, "Third is deprecated");

        PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));
        Optional<Example> value = config.getProvidedParsedValue(property);
        Assertions.assertTrue(value.isPresent());

        List<DeprecatedValueUsage> deprecatedUsages = property.checkForDeprecatedValues(value.get());
        Assertions.assertEquals(deprecatedUsages.size(), 1);
        Assertions.assertEquals(deprecatedUsages.get(0).getValue(), "THIRD");
        Assertions.assertEquals(deprecatedUsages.get(0).getInfo().getValueDescription(), "THIRD");
        Assertions.assertEquals(deprecatedUsages.get(0).getInfo().getReason(), "Third is deprecated");
    }

    @Test
    public void testDeprecatedValueDefaultNotUsage() throws InvalidPropertyException {
        EnumProperty<Example> property = new EnumProperty<>("enum.valued", Example.THIRD, Example.class);
        property.deprecateValue(Example.THIRD, "Third is deprecated");

        PropertyConfiguration config = emptyConfig();
        Optional<Example> value = config.getProvidedParsedValue(property);
        Assertions.assertFalse(value.isPresent());
    }

    @Test
    public void testDeprecatedValueDefaultProvided() throws InvalidPropertyException {
        EnumProperty<Example> property = new EnumProperty<>("enum.valued", Example.THIRD, Example.class);
        property.deprecateValue(Example.THIRD, "Third is deprecated");

        PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));
        Optional<Example> value = config.getProvidedParsedValue(property);
        Assertions.assertTrue(value.isPresent());

        List<DeprecatedValueUsage> deprecatedUsages = property.checkForDeprecatedValues(value.get());
        Assertions.assertEquals(deprecatedUsages.size(), 1);
        Assertions.assertEquals(deprecatedUsages.get(0).getValue(), "THIRD");
        Assertions.assertEquals(deprecatedUsages.get(0).getInfo().getValueDescription(), "THIRD");
        Assertions.assertEquals(deprecatedUsages.get(0).getInfo().getReason(), "Third is deprecated");
    }

    @Test
    public void testList() throws InvalidPropertyException {
        EnumListProperty<Example> property = new EnumListProperty<>("enum.list", Bds.listOf(Example.THIRD), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.list", "ANOTHER,THING"));
        Assertions.assertEquals(Bds.listOf(Example.ANOTHER, Example.THING), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}