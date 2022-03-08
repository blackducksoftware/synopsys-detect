package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

/**
 * A property that returns null when it is not present in a Configuration.
 * @param <R> the type this property returns when it is retrieved from a Configuration.
 * @param <V> the type this property uses when parsing user input.
 */
public abstract class ValuedProperty<V, R> extends TypedProperty<V, R> {
    private final V defaultValue;

    public ValuedProperty(@NotNull String key, @NotNull ValueParser<V> valueParser, V defaultValue) {
        super(key, valueParser);
        this.defaultValue = defaultValue;
    }

    public V getDefaultValue() {
        return defaultValue;
    }

}