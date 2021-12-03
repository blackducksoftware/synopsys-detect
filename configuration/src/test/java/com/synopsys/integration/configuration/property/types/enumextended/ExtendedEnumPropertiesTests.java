package com.synopsys.integration.configuration.property.types.enumextended;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.ValuedListProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class ExtendedEnumPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    private enum ExampleExtension {
        NONE
    }

    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableProperty<ExtendedEnumValue<ExampleExtension, Example>> property = new NullableExtendedEnumProperty<>("enum.nullable", ExampleExtension.class, Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "NONE"));
        Optional<ExtendedEnumValue<ExampleExtension, Example>> value = config.getValue(property);
        Assertions.assertEquals(Optional.of(ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE)), value);

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD", "NONE"));
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        ValuedProperty<ExtendedEnumValue<ExampleExtension, Example>> property = new ExtendedEnumProperty<>("enum.nullable", ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE), ExampleExtension.class, Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER"));
        ExtendedEnumValue<ExampleExtension, Example> value = config.getValue(property);
        Assertions.assertEquals(ExtendedEnumValue.ofBaseValue(Example.ANOTHER), value);

        PropertyTestHelpUtil.assertAllHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD", "NONE"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        List<ExtendedEnumValue<ExampleExtension, Example>> defaultValue = Bds.listOf(ExtendedEnumValue.ofBaseValue(Example.THING), ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE));
        ValuedListProperty<ExtendedEnumValue<ExampleExtension, Example>> property = new ExtendedEnumListProperty<>("enum.nullable", defaultValue, ExampleExtension.class, Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "THIRD,NONE"));
        List<ExtendedEnumValue<ExampleExtension, Example>> value = config.getValue(property);
        Assertions.assertEquals(Bds.listOf(ExtendedEnumValue.ofBaseValue(Example.THIRD), ExtendedEnumValue.ofExtendedValue(ExampleExtension.NONE)), value);

        PropertyTestHelpUtil.assertAllListHelpValid(property, Bds.listOf("THING", "ANOTHER", "THIRD", "NONE"));
    }
}