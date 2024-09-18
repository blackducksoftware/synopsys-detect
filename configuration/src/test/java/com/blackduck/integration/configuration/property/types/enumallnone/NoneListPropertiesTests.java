package com.blackduck.integration.configuration.property.types.enumallnone;

import static com.blackduck.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.ArrayList;
import java.util.Arrays;

import com.blackduck.integration.configuration.config.InvalidPropertyException;
import com.blackduck.integration.configuration.config.PropertyConfiguration;
import com.blackduck.integration.configuration.property.PropertyTestHelpUtil;
import com.blackduck.integration.configuration.property.types.enumallnone.list.NoneEnumList;
import com.blackduck.integration.configuration.property.types.enumallnone.property.NoneEnumListProperty;
import com.blackduck.integration.configuration.util.ConfigTestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NoneListPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testHelp() throws InvalidPropertyException {
        NoneEnumListProperty<Example> property = new NoneEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "NONE"));
    }

    @Test
    public void testNone() throws InvalidPropertyException {
        NoneEnumListProperty<Example> property = new NoneEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = ConfigTestUtils.configOf(Pair.of("enum.valued", "nOnE"));
        NoneEnumList<Example> list = config.getValue(property);

        Assertions.assertTrue(list.containsNone());
        Assertions.assertFalse(list.containsAll());

        Assertions.assertEquals(list.toPresentValues().size(), 0, "No 'actual' valaues of the enum are 'present'");
        Assertions.assertEquals(list.toProvidedValues().size(), 1, "Should have single provided value of NONE.");
        Assertions.assertEquals(list.representedValues().size(), 0, "No values should be represented by NONE.");
    }

    @Test
    public void testSingleValue() throws InvalidPropertyException {
        NoneEnumListProperty<Example> property = new NoneEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = ConfigTestUtils.configOf(Pair.of("enum.valued", "thIrd"));
        NoneEnumList<Example> list = config.getValue(property);

        Assertions.assertFalse(list.containsNone());
        Assertions.assertFalse(list.containsAll());

        Assertions.assertEquals(list.toPresentValues().size(), 1, "Since 'third' was provided, it should be present.");
        Assertions.assertEquals(list.toProvidedValues().size(), 1, "Should have single provided value of 'third'.");
        Assertions.assertEquals(list.representedValues().size(), 1, "One value, 'third', should be represented.");

        Assertions.assertTrue(list.representedValues().contains(Example.THIRD));
    }

    @Test
    public void testTwoValues() throws InvalidPropertyException {
        NoneEnumListProperty<Example> property = new NoneEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = ConfigTestUtils.configOf(Pair.of("enum.valued", "thIrd,another"));
        NoneEnumList<Example> list = config.getValue(property);

        Assertions.assertFalse(list.containsNone());
        Assertions.assertFalse(list.containsAll());

        Assertions.assertEquals(list.toPresentValues().size(), 2, "Since 'third' was provided, it should be present.");
        Assertions.assertEquals(list.toProvidedValues().size(), 2, "Should have two provided values of 'third' and 'another'.");
        Assertions.assertEquals(list.representedValues().size(), 2, "Two values, 'third' and 'another', should be represented.");

        Assertions.assertTrue(list.representedValues().contains(Example.THIRD));
        Assertions.assertTrue(list.representedValues().contains(Example.ANOTHER));
    }

    @Test()
    public void testAllThrows() throws InvalidPropertyException {
        NoneEnumListProperty<Example> property = new NoneEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = ConfigTestUtils.configOf(Pair.of("enum.valued", "aLL"));
        Assertions.assertThrows(InvalidPropertyException.class, () -> config.getValue(property));
    }
}