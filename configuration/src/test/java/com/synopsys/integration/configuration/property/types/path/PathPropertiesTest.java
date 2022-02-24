package com.synopsys.integration.configuration.property.types.path;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static java.util.Collections.emptyList;

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
public class PathPropertiesTest {
    @Test
    public void testNullable() throws InvalidPropertyException {
        NullableAlikeProperty<PathValue> property = new NullablePathProperty("path.nullable");
        PropertyConfiguration config = configOf(Pair.of("path.nullable", "/new/path"));
        Assertions.assertEquals(Optional.of(new PathValue("/new/path")), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        ValuedAlikeProperty<PathValue> property = new PathProperty("path.valued", new PathValue("/tmp/test"));
        PropertyConfiguration config = configOf(Pair.of("path.valued", "/new/path"));
        Assertions.assertEquals(new PathValue("/new/path"), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        ValuedAlikeListProperty<PathValue> property = new PathListProperty("path.list", emptyList());
        PropertyConfiguration config = configOf(Pair.of("path.list", "/new/path,/other/new/path"));
        Assertions.assertEquals(Bds.listOf(new PathValue("/new/path"), new PathValue("/other/new/path")), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}