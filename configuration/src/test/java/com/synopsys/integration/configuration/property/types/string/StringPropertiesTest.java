package com.synopsys.integration.configuration.property.types.string;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;

import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.Bds;
import com.synopsys.integration.configuration.config.InvalidPropertyException;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.PropertyTestHelpUtil;
import com.synopsys.integration.configuration.property.base.NullableAlikeProperty;
import com.synopsys.integration.configuration.property.base.ValuedAlikeListProperty;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class StringPropertiesTest {
    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableAlikeProperty<String> property = new NullableStringProperty("string.nullable");
        PropertyConfiguration config = configOf(Pair.of("string.nullable", "abc"));
        Assertions.assertEquals(Optional.of("abc"), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        ValuedAlikeProperty<String> property = new StringProperty("string.valued", "defaultString");
        PropertyConfiguration config = configOf(Pair.of("string.valued", "abc"));
        Assertions.assertEquals("abc", config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        ValuedAlikeListProperty<String> property = new StringListProperty("string.list", Collections.emptyList());
        PropertyConfiguration config = configOf(Pair.of("string.list", "1,2,3,abc"));
        Assertions.assertEquals(Bds.listOf("1", "2", "3", "abc"), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}