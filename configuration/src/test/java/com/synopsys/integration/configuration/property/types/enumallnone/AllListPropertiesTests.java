package com.synopsys.integration.configuration.property.types.enumallnone;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.types.enumallnone.list.AllEnumList;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllEnumListProperty;

public class AllListPropertiesTests {
    private enum Example {
        THING,
        ANOTHER,
        THIRD
    }

    @Test
    public void testHelp() throws InvalidPropertyException {
        AllEnumListProperty<Example> property = new AllEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyTestHelpUtil.assertAllHelpValid(property, Arrays.asList("THING", "ANOTHER", "THIRD", "ALL"));
    }

    @Test
    public void testAll() throws InvalidPropertyException {
        AllEnumListProperty<Example> property = new AllEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "ALL"));
        AllEnumList<Example> list = config.getValue(property);

        Assertions.assertTrue(list.containsAll());
        Assertions.assertFalse(list.containsNone());

        Assertions.assertEquals(list.toPresentValues().size(), 0, "No 'actual' valaues of the enum are 'present'");
        Assertions.assertEquals(list.toProvidedValues().size(), 1, "Should have single provided value of ALL.");
        Assertions.assertEquals(list.representedValues().size(), 3, "All 3 values should be represented by ALL.");
    }

    @Test
    public void testSingleValue() throws InvalidPropertyException {
        AllEnumListProperty<Example> property = new AllEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "thIrd"));
        AllEnumList<Example> list = config.getValue(property);

        Assertions.assertFalse(list.containsNone());
        Assertions.assertFalse(list.containsAll());

        Assertions.assertEquals(list.toPresentValues().size(), 1, "Since 'third' was provided, it should be present.");
        Assertions.assertEquals(list.toProvidedValues().size(), 1, "Should have single provided value of 'third'.");
        Assertions.assertEquals(list.representedValues().size(), 1, "One value, 'third', should be represented.");

        Assertions.assertTrue(list.representedValues().contains(Example.THIRD));
    }

    @Test
    public void testTwoValues() throws InvalidPropertyException {
        AllEnumListProperty<Example> property = new AllEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "thIrd,another"));
        AllEnumList<Example> list = config.getValue(property);

        Assertions.assertFalse(list.containsNone());
        Assertions.assertFalse(list.containsAll());

        Assertions.assertEquals(list.toPresentValues().size(), 2, "Since 'third' was provided, it should be present.");
        Assertions.assertEquals(list.toProvidedValues().size(), 2, "Should have two provided values of 'third' and 'another'.");
        Assertions.assertEquals(list.representedValues().size(), 2, "Two values, 'third' and 'another', should be represented.");

        Assertions.assertTrue(list.representedValues().contains(Example.THIRD));
        Assertions.assertTrue(list.representedValues().contains(Example.ANOTHER));
    }

    @Test()
    public void testNoneThrows() throws InvalidPropertyException {
        AllEnumListProperty<Example> property = new AllEnumListProperty<>("enum.valued", new ArrayList<>(), Example.class);
        PropertyConfiguration config = configOf(Pair.of("enum.valued", "none"));
        Assertions.assertThrows(InvalidPropertyException.class, () -> config.getValue(property));
    }
}