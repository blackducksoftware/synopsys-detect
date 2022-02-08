package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

/**
 * A property that returns null when it is not present in a Configuration.
 * @param <V> the type this property returns when it is retrieved from a Configuration.
 */
public abstract class NullableProperty<V, R> extends TypedProperty<V, R> {
    public NullableProperty(@NotNull String key, @NotNull ValueParser<V> parser) {
        super(key, parser);
    }
}