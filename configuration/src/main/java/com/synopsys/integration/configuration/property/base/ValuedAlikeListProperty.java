package com.synopsys.integration.configuration.property.base;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;

/**
 * This is a property with a key and with a default value, it will always have a value.
 */
// Using @JvmSuppressWildcards to prevent the Kotlin compiler from generating wildcard types: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics
public abstract class ValuedAlikeListProperty<V> extends ValuedListProperty<V, List<V>> {
    public ValuedAlikeListProperty(@NotNull String key, @NotNull ValueParser<List<V>> valueParser, List<V> defaultValue) {
        super(key, valueParser, defaultValue);
    }

    @Override
    @NotNull
    public List<V> convertValue(List<V> value) {
        return value;
    }
}