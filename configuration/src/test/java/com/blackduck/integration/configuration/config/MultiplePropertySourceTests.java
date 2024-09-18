package com.blackduck.integration.configuration.config;

import static com.blackduck.integration.configuration.util.ConfigTestUtils.configOf;
import static com.blackduck.integration.configuration.util.ConfigTestUtils.propertySourceOf;

import java.util.Optional;

import com.blackduck.integration.configuration.util.ConfigTestUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.blackduck.integration.common.util.Bds;
import com.blackduck.integration.configuration.property.base.NullableAlikeProperty;
import com.blackduck.integration.configuration.property.types.string.NullableStringProperty;
import com.blackduck.integration.configuration.source.PropertySource;

class MultiplePropertySourceTests {
    @Test
    public void primaryValueUsedOverSecondary() throws InvalidPropertyException {
        NullableAlikeProperty<String> sharedProperty = new NullableStringProperty("shared.key");
        PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(sharedProperty.getKey(), "secondaryValue"));
        PropertySource primarySource = propertySourceOf("primaryName", Pair.of(sharedProperty.getKey(), "primaryValue"));

        PropertyConfiguration config = ConfigTestUtils.configOf(primarySource, secondarySource);
        Assertions.assertEquals(Optional.of("primaryValue"), config.getValue(sharedProperty));
        Assertions.assertEquals(Optional.of("primaryName"), config.getPropertySource(sharedProperty));
    }

    @Test
    public void valueFromSecondaryWhenNotInPrimary() throws InvalidPropertyException {
        NullableAlikeProperty<String> property = new NullableStringProperty("any.key");
        PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(property.getKey(), "secondaryValue"));
        PropertySource primarySource = ConfigTestUtils.propertySourceOf("primaryName");

        PropertyConfiguration config = ConfigTestUtils.configOf(primarySource, secondarySource);
        Assertions.assertEquals(Optional.of("secondaryValue"), config.getValue(property));
        Assertions.assertEquals(Optional.of("secondaryName"), config.getPropertySource(property));
    }

    @Test
    public void containsKeysFromBothSources() {
        NullableAlikeProperty<String> primaryProperty = new NullableStringProperty("primary.key");
        PropertySource primarySource = propertySourceOf("primaryName", Pair.of(primaryProperty.getKey(), "primaryValue"));

        NullableAlikeProperty<String> secondaryProperty = new NullableStringProperty("secondary.key");
        PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(secondaryProperty.getKey(), "secondaryValue"));

        PropertyConfiguration config = ConfigTestUtils.configOf(primarySource, secondarySource);
        Assertions.assertEquals(Bds.setOf(primaryProperty.getKey(), secondaryProperty.getKey()), config.getKeys());
    }

    @Test
    public void rawValueMapContainsValuesFromBothSources() {
        NullableAlikeProperty<String> primaryProperty = new NullableStringProperty("primary.key");
        PropertySource primarySource = propertySourceOf("primaryName", Pair.of(primaryProperty.getKey(), "primaryValue"));

        NullableAlikeProperty<String> secondaryProperty = new NullableStringProperty("secondary.key");
        PropertySource secondarySource = propertySourceOf("secondaryName", Pair.of(secondaryProperty.getKey(), "secondaryValue"));

        PropertyConfiguration config = ConfigTestUtils.configOf(primarySource, secondarySource);
        Assertions.assertEquals(Bds.mapOf(
            Pair.of(primaryProperty.getKey(), "primaryValue"),
            Pair.of(secondaryProperty.getKey(), "secondaryValue")
        ), config.getRaw());
    }
}