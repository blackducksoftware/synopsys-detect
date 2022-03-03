package com.synopsys.integration.configuration.property.types.enumsoft;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
class SoftEnumPropertiesTest {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testNullableActualValue() throws InvalidPropertyException {
        NullableSoftEnumProperty<Example> property = new NullableSoftEnumProperty<>("enum.nullable", Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER"));
        Assertions.assertEquals(Optional.of(SoftEnumValue.ofEnumValue(Example.ANOTHER)), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testNullableStringValue() throws InvalidPropertyException {
        NullableSoftEnumProperty<Example> property = new NullableSoftEnumProperty<>("enum.nullable", Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.nullable", "ANOTHER ONE"));
        Assertions.assertEquals(Optional.of(SoftEnumValue.ofSoftValue("ANOTHER ONE")), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testValuedActualValue() throws InvalidPropertyException {
        SoftEnumProperty<Example> property = new SoftEnumProperty<>("enum.valued", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD"));
        Assertions.assertEquals(SoftEnumValue.ofEnumValue(Example.THIRD), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testValuedStringValue() throws InvalidPropertyException {
        SoftEnumProperty<Example> property = new SoftEnumProperty<>("enum.valued", SoftEnumValue.ofEnumValue(Example.ANOTHER), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "THIRD ONE"));
        Assertions.assertEquals(SoftEnumValue.ofSoftValue("THIRD ONE"), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD"));
    }

    @Test
    public void testList() throws InvalidPropertyException {
        SoftEnumListProperty<Example> property = new SoftEnumListProperty<>("enum.list", Collections.singletonList(SoftEnumValue.ofEnumValue(Example.THIRD)), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.list", "ANOTHER,THING,test"));
        Assertions.assertEquals(
            Arrays.asList(SoftEnumValue.ofEnumValue(Example.ANOTHER), SoftEnumValue.ofEnumValue(Example.THING), SoftEnumValue.ofSoftValue("test")),
            config.getValue(property)
        );

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}