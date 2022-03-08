package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

/**
 * A property that returns null when it is not present in a Configuration.
 * @param <V> the type this property returns when it is retrieved from a Configuration and when parsing user input.
 */
public abstract class ValuedAlikeProperty<V> extends ValuedProperty<V, V> {
    public ValuedAlikeProperty(@NotNull String key, @NotNull ValueParser<V> valueParser, V defaultValue) {
        super(key, valueParser, defaultValue);
    }

    @Override
    @NotNull
    public V convertValue(V value) {
        return value;
    }
}