package com.synopsys.integration.configuration.property.types.integer;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.base.NullableAlikeProperty;
import com.synopsys.integration.configuration.property.base.ValuedAlikeListProperty;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class IntegerPropertiesTests {
    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableAlikeProperty<Integer> property = new NullableIntegerProperty("integer.nullable");
        PropertyConfiguration config = configOf(Pair.of("integer.nullable", "2"));
        Assertions.assertEquals(Optional.of(2), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        ValuedAlikeProperty<Integer> property = new IntegerProperty("integer.valued", 2);
        PropertyConfiguration config = configOf(Pair.of("integer.valued", "5"));
        Assertions.assertEquals(new Integer(5), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        ValuedAlikeListProperty<Integer> property = new IntegerListProperty("integer.list", Collections.emptyList());
        PropertyConfiguration config = configOf(Pair.of("integer.list", "2,3"));
        Assertions.assertEquals(Bds.listOf(2, 3), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}