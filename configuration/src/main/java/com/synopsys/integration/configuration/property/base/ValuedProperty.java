package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

/**
 * A property that returns null when it is not present in a Configuration.
 * @param <T> the type this property returns when it is retrieved from a Configuration.
 */
public abstract class ValuedProperty<T> extends TypedProperty<T> {
    private T defaultValue;

    public ValuedProperty(@NotNull String key, @NotNull ValueParser<T> valueParser, T defaultValue) {
        super(key, valueParser);
        this.defaultValue = defaultValue;
    }

    public T getDefaultValue() {
        return defaultValue;
    }
}