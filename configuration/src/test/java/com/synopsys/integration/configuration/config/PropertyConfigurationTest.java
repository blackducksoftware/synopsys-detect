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
import com.synopsys.integration.configuration.property.base.NullableAlikeProperty;
import com.synopsys.integration.configuration.property.base.PassthroughProperty;
import com.synopsys.integration.configuration.property.base.ValuedAlikeProperty;
import com.synopsys.integration.configuration.property.types.string.NullableStringProperty;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.configuration.util.ProductMajorVersion;

public class PropertyConfigurationTest {
    public static final String UNKNOWN_VALUE = "-1";

    private static class TestValueParser extends ValueParser<String> {
        @NotNull
        @Override
        public String parse(@NotNull String value) throws ValueParseException {
            if (UNKNOWN_VALUE.equals(value)) {
                throw new ValueParseException(value, "String", "Will parse any value to String, except for '-1' for the test.");
            }
            return value;
        }
    }

    private static class NullableTestProperty extends NullableAlikeProperty<String> {
        public NullableTestProperty(@NotNull String key) {
            super(key, new TestValueParser());
        }
    }

    private static class ValuedTestProperty extends ValuedAlikeProperty<String> {
        public ValuedTestProperty(@NotNull String key, String defaultValue) {
            super(key, new TestValueParser(), defaultValue);
        }
    }

    //#region Recommended Usage

    @Test
    public void getValueOrNull() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(
            Optional.empty(),
            configOf(Pair.of(NullableAlikeProperty.getKey(), UNKNOWN_VALUE)).getValueOrEmpty(NullableAlikeProperty),
            "An unknown value should fail to parse and the config should provide null."
        );
    }

    @Test
    public void getValueOrDefault() {
        ValuedAlikeProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertEquals(propertyWithDefault.getDefaultValue(), configOf(Pair.of(propertyWithDefault.getKey(), UNKNOWN_VALUE)).getValueOrDefault(propertyWithDefault),
            "An unknown value should fail to parse and the config should provide the default value."
        );
    }

    @Test
    public void getValueNullableValue() throws InvalidPropertyException {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        PropertyConfiguration config = configOf(Pair.of(NullableAlikeProperty.getKey(), "providedValue"));
        Assertions.assertEquals(Optional.of("providedValue"), config.getValue(NullableAlikeProperty), "A provided nullable property should return the provided value.");
    }

    @Test
    public void getValueThrowsOnParseFailureNullable() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertThrows(
            InvalidPropertyException.class,
            () -> configOf(Pair.of(NullableAlikeProperty.getKey(), UNKNOWN_VALUE)).getValue(NullableAlikeProperty),
            "Should throw an exception when failing to parse."
        );
    }

    @Test
    public void getValueNullableNull() throws InvalidPropertyException {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(Optional.empty(), emptyConfig().getValue(NullableAlikeProperty), "Config should provide an empty Optional if the property is nullable.");
    }

    @Test
    public void getValueOverridesDefault() throws InvalidPropertyException {
        ValuedAlikeProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        PropertyConfiguration config = configOf(Pair.of(propertyWithDefault.getKey(), "overridden"));
        Assertions.assertEquals("overridden", config.getValue(propertyWithDefault), "A valid provided value should override any default value.");
    }

    @Test()
    public void getValueThrowsOnParseFailureTyped() {
        ValuedAlikeProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertThrows(
            InvalidPropertyException.class,
            () -> configOf(Pair.of(propertyWithDefault.getKey(), UNKNOWN_VALUE)).getValue(propertyWithDefault),
            "Should throw an exception when failing to parse."
        );
    }

    @Test
    public void getValueProvidesDefault() throws InvalidPropertyException {
        ValuedAlikeProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertEquals(
            propertyWithDefault.getDefaultValue(),
            emptyConfig().getValue(propertyWithDefault),
            "Config should provide default value when property is not provided."
        );
    }

    @Test()
    public void wasKeyProvided() {
        final String exampleKey = "example.key";
        Assertions.assertTrue(configOf(Pair.of(exampleKey, UNKNOWN_VALUE)).wasKeyProvided(exampleKey), "The key was provided.");
        Assertions.assertFalse(emptyConfig().wasKeyProvided(exampleKey), "The key was not provided.");
    }

    @Test()
    public void wasPropertyProvided() {
        ValuedAlikeProperty<String> propertyWithDefault = new ValuedTestProperty("example.key", "defaultValue");
        Assertions.assertTrue(configOf(Pair.of(propertyWithDefault.getKey(), UNKNOWN_VALUE)).wasPropertyProvided(propertyWithDefault), "The property was provided.");
        Assertions.assertFalse(emptyConfig().wasPropertyProvided(propertyWithDefault), "The property was not provided.");
    }

    @Test
    public void getPropertySource() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(
            Optional.of("map"),
            configOf(Pair.of(NullableAlikeProperty.getKey(), UNKNOWN_VALUE)).getPropertySource(NullableAlikeProperty),
            "The source of this property should exist."
        );
        Assertions.assertEquals(Optional.empty(), emptyConfig().getPropertySource(NullableAlikeProperty), "The property is not provided and therefore should not have a source.");
    }

    @Test
    public void getPropertyOrigin() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(
            Optional.of("map"),
            configOf(Pair.of(NullableAlikeProperty.getKey(), UNKNOWN_VALUE)).getPropertyOrigin(NullableAlikeProperty),
            "The property was provided and should have an origin."
        );
        Assertions.assertEquals(Optional.empty(), emptyConfig().getPropertyOrigin(NullableAlikeProperty), "The property was not provided and should not have an origin.");
    }

    @Test
    public void getKeys() {
        Assertions.assertEquals(
            Bds.setOf("example.key", "other.key"),
            configOf(Pair.of("example.key", UNKNOWN_VALUE), Pair.of("other.key", UNKNOWN_VALUE)).getKeys(),
            "The set of keys returned is not identical to all those provided."
        );
        Assertions.assertEquals(Collections.emptySet(), emptyConfig().getKeys(), "The property was not provided and should not have an origin.");
    }

    @Test
    public void getPropertyException() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertTrue(
            configOf(Pair.of(NullableAlikeProperty.getKey(), UNKNOWN_VALUE)).getPropertyException(NullableAlikeProperty).isPresent(),
            "The property value should not parse successfully."
        );
        Assertions.assertEquals(
            Optional.empty(),
            configOf(Pair.of(NullableAlikeProperty.getKey(), "something")).getPropertyException(NullableAlikeProperty),
            "The property was provided and should be parsable."
        );
        Assertions.assertEquals(
            Optional.empty(),
            emptyConfig().getPropertyException(NullableAlikeProperty),
            "The property was not provided and should not have an exception value."
        );
    }

    //#endregion Recommended Usage

    //region Advanced Usage

    @Test
    public void getRawFromProperty() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        Assertions.assertEquals(
            Optional.of(" true "),
            configOf(Pair.of(NullableAlikeProperty.getKey(), " true ")).getRaw(NullableAlikeProperty),
            "The property should be resolved."
        );
        Assertions.assertEquals(Optional.empty(), emptyConfig().getRaw(NullableAlikeProperty), "The property was not provided and should not resolve a value.");
    }

    @Test
    public void getRaw() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        ValuedAlikeProperty<String> valuedProperty = new ValuedTestProperty("property.two.key", "test");

        Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(NullableAlikeProperty.getKey(), " true "),
            Pair.of(valuedProperty.getKey(), "")
        );

        PropertyConfiguration config = configOf(propertyMap);
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

        Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(password.getKey(), "password"),
            Pair.of(username.getKey(), "username")
        );

        PropertyConfiguration configuration = configOf(propertyMap);

        Map<String, String> rawPropertyValues = configuration.getMaskedRawValueMap(properties, rawKey -> rawKey.contains("password"));

        Assertions.assertEquals("********", rawPropertyValues.get("blackduck.password"));
        Assertions.assertEquals("username", rawPropertyValues.get("blackduck.username"));
    }

    @Test
    public void getRawValueMapWithPassthroughs() {
        Set<Property> properties = new HashSet<>();
        PassthroughProperty passthrough = new PassthroughProperty("blackduck.passthrough");
        properties.add(passthrough);

        Map<String, String> propertyMap = Bds.mapOf(
            Pair.of("blackduck.passthrough.password", "password")
        );

        PropertyConfiguration configuration = configOf(propertyMap);

        Map<String, String> rawPropertyValues = configuration.getMaskedRawValueMap(properties, rawKey -> rawKey.contains("password"));

        Assertions.assertEquals("********", rawPropertyValues.get("blackduck.passthrough.password"));
    }

    @Test
    public void getRawFromKeys() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        ValuedAlikeProperty<String> valuedProperty = new ValuedTestProperty("property.two.key", "test");
        Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(NullableAlikeProperty.getKey(), " true "),
            Pair.of(valuedProperty.getKey(), "")
        );
        PropertyConfiguration config = configOf(propertyMap);

        Assertions.assertEquals(propertyMap, config.getRaw(propertyMap.keySet()), "All keys should match.");

        Set<String> extraKeys = Bds.setOf("unrelated.key");
        extraKeys.addAll(propertyMap.keySet());
        Assertions.assertEquals(propertyMap, config.getRaw(extraKeys), "Expected entries should not include any unrelated keys.");

        Assertions.assertEquals(emptyMap(), config.getRaw(Bds.setOf("unrelated.key")), "The config should not provide any values.");
        Assertions.assertEquals(emptyMap(), config.getRaw(Bds.setOf()), "The config should not provide any values.");
    }

    @Test
    public void getRawFromPredicate() {
        NullableAlikeProperty<String> NullableAlikeProperty = new NullableTestProperty("example.key");
        ValuedAlikeProperty<String> valuedProperty = new ValuedTestProperty("property.two.key", "test");
        Map<String, String> propertyMap = Bds.mapOf(
            Pair.of(NullableAlikeProperty.getKey(), " true "),
            Pair.of(valuedProperty.getKey(), "")
        );
        PropertyConfiguration config = configOf(propertyMap);

        Assertions.assertEquals(propertyMap, config.getRaw(it -> true), "All keys should match.");

        Assertions.assertEquals(
            1,
            config.getRaw(propertyKey -> propertyKey.equals(NullableAlikeProperty.getKey())).size(),
            "Expected entries should not include any unrelated keys."
        );

        Assertions.assertEquals(emptyMap(), config.getRaw(it -> false), "The config should not provide any values to provide.");
    }

    @Test
    public void getRawPassthroughMultipleValues() {
        PassthroughProperty passthrough = new PassthroughProperty("pass");
        PropertySource secondarySource = propertySourceOf("secondary", Pair.of("pass.two", "two value"), Pair.of("ignore", "ignore value"));
        PropertySource primarySource = propertySourceOf("primary", Pair.of("pass.one", "one value"));
        PropertyConfiguration configuration = configOf(primarySource, secondarySource);

        Map<String, String> properties = Bds.mapOf(Pair.of("one", "one value"), Pair.of("two", "two value"));
        Assertions.assertEquals(properties, configuration.getRaw(passthrough));
    }

    @Test
    public void getRawPassthroughPrimary() {
        PassthroughProperty passthrough = new PassthroughProperty("pass");
        PropertySource secondarySource = propertySourceOf("secondary", Pair.of("pass.shared", "secondaryValue"));
        PropertySource primarySource = propertySourceOf("primary", Pair.of("pass.shared", "primaryValue"));
        PropertyConfiguration configuration = configOf(primarySource, secondarySource);

        Map<String, String> properties = Bds.mapOf(Pair.of("shared", "primaryValue"));
        Assertions.assertEquals(properties, configuration.getRaw(passthrough));
    }

    @Test
    public void testPropertyIsDeprecated() {
        Property property1 = new NullableStringProperty("name");
        property1.setRemovalDeprecation("desc", new ProductMajorVersion(1));
        Assertions.assertTrue(property1.isDeprecatedForRemoval());

        Property property2 = new NullableStringProperty("name");
        property2.addDeprecatedValueInfo("val", "reason");
        Assertions.assertFalse(property2.isDeprecatedForRemoval());
    }

    //endregion Advanced Usage
}