/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.configuration.config;

import static com.synopsys.integration.configuration.util.ConfigTestUtils.configOf;
import static com.synopsys.integration.configuration.util.ConfigTestUtils.emptyConfig;
import static com.synopsys.integration.configuration.util.ConfigTestUtils.propertySourceOf;
import static java.util.Collections.emptyMap;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.parse.ValueParseException;
import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.PassthroughProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.source.PropertySource;

public class PropertyConfigurationTest {
    public static final String UNKNOWN_VALUE = "-1";

    private static class TestValueParser extends ValueParser<String> {
        @NotNull
        @Override
        public String parse(@NotNull final String value) throws ValueParseException {
            if (UNKNOWN_VALUE.equals(value)) {
                throw new ValueParseException(value, "String", "Will parse any value to String, except for '-1' for the test.");
            }
            return value;
        }
    }

    private static class NullableTestProperty extends NullableProperty<String> {
        public NullableTestProperty(@NotNull final String key) {
            super(key, new TestValueParser());
        }
    }

    private static class ValuedTestProperty extends ValuedProperty<String> {
        public ValuedTestProperty(@NotNull final String key, final String defaultValue) {
            super(key, new TestValueParser(), defaultValue);
        }
    }

    //#region Recommended Usage

    @Test
    public void getValueOrNull() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(Optional.empty(), configOf(Pair.of(nullableProperty.getKey(), UNKNOWN_VALUE)).getValueOrEmpty(nullableProperty), "An unknown value should fail to parse and the config should provide null.");
    }

    @Test
    public void getValueOrDefault() {
        final ValuedProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertEquals(propertyWithDefault.getDefaultValue(), configOf(Pair.of(propertyWithDefault.getKey(), UNKNOWN_VALUE)).getValueOrDefault(propertyWithDefault),
            "An unknown value should fail to parse and the config should provide the default value.");
    }

    @Test
    public void getValueNullableValue() throws InvalidPropertyException {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        final PropertyConfiguration config = configOf(Pair.of(nullableProperty.getKey(), "providedValue"));
        Assertions.assertEquals(Optional.of("providedValue"), config.getValue(nullableProperty), "A provided nullable property should return the provided value.");
    }

    @Test
    public void getValueThrowsOnParseFailureNullable() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertThrows(InvalidPropertyException.class, () -> configOf(Pair.of(nullableProperty.getKey(), UNKNOWN_VALUE)).getValue(nullableProperty), "Should throw an exception when failing to parse.");
    }

    @Test
    public void getValueNullableNull() throws InvalidPropertyException {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(Optional.empty(), emptyConfig().getValue(nullableProperty), "Config should provide an empty Optional if the property is nullable.");
    }

    @Test
    public void getValueOverridesDefault() throws InvalidPropertyException {
        final ValuedProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        final PropertyConfiguration config = configOf(Pair.of(propertyWithDefault.getKey(), "overridden"));
        Assertions.assertEquals("overridden", config.getValue(propertyWithDefault), "A valid provided value should override any default value.");
    }

    @Test()
    public void getValueThrowsOnParseFailureTyped() {
        final ValuedProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertThrows(InvalidPropertyException.class, () -> configOf(Pair.of(propertyWithDefault.getKey(), UNKNOWN_VALUE)).getValue(propertyWithDefault), "Should throw an exception when failing to parse.");
    }

    @Test
    public void getValueProvidesDefault() throws InvalidPropertyException {
        final ValuedProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertEquals(propertyWithDefault.getDefaultValue(), emptyConfig().getValue(propertyWithDefault), "Config should provide default value when property is not provided.");
    }

    @Test()
    public void wasKeyProvided() {
        final String exampleKey = "example.key";
        Assertions.assertTrue(configOf(Pair.of(exampleKey, UNKNOWN_VALUE)).wasKeyProvided(exampleKey), "The key was provided.");
        Assertions.assertFalse(emptyConfig().wasKeyProvided(exampleKey), "The key was not provided.");
    }

    @Test()
    public void wasPropertyProvided() {
        final ValuedProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertTrue(configOf(Pair.of(propertyWithDefault.getKey(), UNKNOWN_VALUE)).wasPropertyProvided(propertyWithDefault), "The property was provided.");
        Assertions.assertFalse(emptyConfig().wasPropertyProvided(propertyWithDefault), "The property was not provided.");
    }

    @Test
    public void getPropertySource() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(Optional.of("map"), configOf(Pair.of(nullableProperty.getKey(), UNKNOWN_VALUE)).getPropertySource(nullableProperty), "The source of this property should exist.");
        Assertions.assertEquals(Optional.empty(), emptyConfig().getPropertySource(nullableProperty), "The property is not provided and therefore should not have a source.");
    }

    @Test
    public void getPropertyOrigin() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(Optional.of("map"), configOf(Pair.of(nullableProperty.getKey(), UNKNOWN_VALUE)).getPropertyOrigin(nullableProperty), "The property was provided and should have an origin.");
        Assertions.assertEquals(Optional.empty(), emptyConfig().getPropertyOrigin(nullableProperty), "The property was not provided and should not have an origin.");
    }

    @Test
    public void getKeys() {
        Assertions.assertEquals(Bds.setOf("example.key", "other.key"), configOf(Pair.of("example.key", UNKNOWN_VALUE), Pair.of("other.key", UNKNOWN_VALUE)).getKeys(), "The set of keys returned is not identical to all those provided.");
        Assertions.assertEquals(Collections.emptySet(), emptyConfig().getKeys(), "The property was not provided and should not have an origin.");
    }

    @Test
    public void getPropertyException() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertTrue(configOf(Pair.of(nullableProperty.getKey(), UNKNOWN_VALUE)).getPropertyException(nullableProperty).isPresent(), "The property value should not parse successfully.");
        Assertions.assertEquals(Optional.empty(), configOf(Pair.of(nullableProperty.getKey(), "something")).getPropertyException(nullableProperty), "The property was provided and should be parsable.");
        Assertions.assertEquals(Optional.empty(), emptyConfig().getPropertyException(nullableProperty), "The property was not provided and should not have an exception value.");
    }

    //#endregion Recommended Usage

    //region Advanced Usage

    @Test
    public void getRawFromProperty() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(Optional.of(" true "), configOf(Pair.of(nullableProperty.getKey(), " true ")).getRaw(nullableProperty), "The property should be resolved.");
        Assertions.assertEquals(Optional.empty(), emptyConfig().getRaw(nullableProperty), "The property was not provided and should not resolve a value.");
    }

    @Test
    public void getRaw() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        final ValuedProperty<String> valuedProperty = new ValuedTestProperty("property.two.key", "test");

        final Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(nullableProperty.getKey(), " true "),
            Pair.of(valuedProperty.getKey(), "")
        );

        final PropertyConfiguration config = configOf(propertyMap);
        Assertions.assertEquals(propertyMap, config.getRaw(), "The map provided by the config should match the property source it was given.");
        Assertions.assertEquals(emptyMap(), emptyConfig().getRaw(), "The config should not have any values to provide.");
    }

    @Test
    public void getRawValueMap() {
        Set<Property> properties = new HashSet<>();
        NullableTestProperty password = new NullableTestProperty("blackduck.password");
        NullableTestProperty username = new NullableTestProperty("blackduck.username");
        properties.add(password);
        properties.add(username);

        final Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(password.getKey(), "password"),
            Pair.of(username.getKey(), "username")
        );

        PropertyConfiguration configuration = configOf(propertyMap);

        Map<String, String> rawPropertyValues = configuration.getMaskedRawValueMap(properties, rawKey -> rawKey.contains("password"));

        Assertions.assertEquals("********", rawPropertyValues.get("blackduck.password"));
        Assertions.assertEquals("username", rawPropertyValues.get("blackduck.username"));
    }

    @Test
    public void getRawFromKeys() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        final ValuedProperty<String> valuedProperty = new ValuedTestProperty("property.two.key", "test");
        final Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(nullableProperty.getKey(), " true "),
            Pair.of(valuedProperty.getKey(), "")
        );
        final PropertyConfiguration config = configOf(propertyMap);

        Assertions.assertEquals(propertyMap, config.getRaw(propertyMap.keySet()), "All keys should match.");

        final Set<String> extraKeys = Bds.setOf("unrelated.key");
        extraKeys.addAll(propertyMap.keySet());
        Assertions.assertEquals(propertyMap, config.getRaw(extraKeys), "Expected entries should not include any unrelated keys.");

        Assertions.assertEquals(emptyMap(), config.getRaw(Bds.setOf("unrelated.key")), "The config should not provide any values.");
        Assertions.assertEquals(emptyMap(), config.getRaw(Bds.setOf()), "The config should not provide any values.");
    }

    @Test
    public void getRawFromPredicate() {
        final NullableProperty<String> nullableProperty = new NullableTestProperty("example.key");
        final ValuedProperty<String> valuedProperty = new ValuedTestProperty("property.two.key", "test");
        final Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(nullableProperty.getKey(), " true "),
            Pair.of(valuedProperty.getKey(), "")
        );
        final PropertyConfiguration config = configOf(propertyMap);

        Assertions.assertEquals(propertyMap, config.getRaw(it -> true), "All keys should match.");

        Assertions.assertEquals(1, config.getRaw(propertyKey -> propertyKey.equals(nullableProperty.getKey())).size(), "Expected entries should not include any unrelated keys.");

        Assertions.assertEquals(emptyMap(), config.getRaw(it -> false), "The config should not provide any values to provide.");
    }

    @Test
    public void getRawPassthroughMutlipleValues() {
        final PassthroughProperty passthrough = new PassthroughProperty("pass");
        final PropertySource secondarySource = propertySourceOf("secondary", Pair.of("pass.two", "two value"), Pair.of("ignore", "ignore value"));
        final PropertySource primarySource = propertySourceOf("primary", Pair.of("pass.one", "one value"));
        final PropertyConfiguration configuration = configOf(primarySource, secondarySource);

        final Map<String, String> properties = Bds.mapOf(Pair.of("one", "one value"), Pair.of("two", "two value"));
        Assertions.assertEquals(properties, configuration.getRaw(passthrough));
    }

    @Test
    public void getRawPassthroughPrimary() {
        final PassthroughProperty passthrough = new PassthroughProperty("pass");
        final PropertySource secondarySource = propertySourceOf("secondary", Pair.of("pass.shared", "secondaryValue"));
        final PropertySource primarySource = propertySourceOf("primary", Pair.of("pass.shared", "primaryValue"));
        final PropertyConfiguration configuration = configOf(primarySource, secondarySource);

        final Map<String, String> properties = Bds.mapOf(Pair.of("shared", "primaryValue"));
        Assertions.assertEquals(properties, configuration.getRaw(passthrough));
    }

    //endregion Advanced Usage
}