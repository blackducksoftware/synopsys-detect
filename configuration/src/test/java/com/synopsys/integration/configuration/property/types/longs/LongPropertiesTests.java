package com.synopsys.integration.configuration.property.types.longs;

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
public class LongPropertiesTests {
    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableAlikeProperty<Long> property = new NullableLongProperty("long.nullable");
        PropertyConfiguration config = configOf(Pair.of("long.nullable", "2"));
        Assertions.assertEquals(Optional.of(2L), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        ValuedAlikeProperty<Long> property = new LongProperty("long.valued", 2L);
        PropertyConfiguration config = configOf(Pair.of("long.valued", "5"));
        Assertions.assertEquals(new Long(5L), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        ValuedAlikeListProperty<Long> property = new LongListProperty("long.list", Collections.emptyList());
        PropertyConfiguration config = configOf(Pair.of("long.list", "2,3"));
        Assertions.assertEquals(Bds.listOf(2L, 3L), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}