package com.blackduck.integration.configuration.property.base;

import com.blackduck.integration.configuration.parse.ValueParser;
import org.jetbrains.annotations.NotNull;

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