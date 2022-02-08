package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyBuilder;

/**
 * A property whose values are all prefixed with a common key.
 *
 * The key is lowercase and dot separated, ending with a dot. For example "docker."
 * When retrieved from a Configuration, keys will be returned without the starting prefix. For example "docker.enabled.key" should be returned as "enabled.key" when the key is "docker."
 */
public class PassthroughProperty extends Property {
    public PassthroughProperty(@NotNull String key) {
        super(key);
    }

    public static PropertyBuilder<PassthroughProperty> newBuilder(@NotNull String key) {
        return new PropertyBuilder<PassthroughProperty>().setCreator(() -> new PassthroughProperty(key));
    }

    public String trimKey(String givenKey) {
        return givenKey.substring(getKey().length() + 1);
    }
}