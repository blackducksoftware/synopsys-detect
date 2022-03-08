package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

/**
 * A property that returns null when it is not present in a Configuration.
 * @param <V> the type this property returns when it is retrieved from a Configuration.
 */
public abstract class NullableAlikeProperty<V> extends NullableProperty<V, V> {
    public NullableAlikeProperty(@NotNull String key, @NotNull ValueParser<V> parser) {
        super(key, parser);
    }

    @Override
    @NotNull
    public V convertValue(V value) {
        return value;
    }
}