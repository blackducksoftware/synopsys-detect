package com.synopsys.integration.configuration.property.base;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.util.PropertyUtils;

/**
 * This is a property with a key and with a default value, it will always have a value.
 */
// Using @JvmSuppressWildcards to prevent the Kotlin compiler from generating wildcard types: https://kotlinlang.org/docs/reference/java-to-kotlin-interop.html#variant-generics
public abstract class ValuedListProperty<V, R> extends ValuedProperty<List<V>, R> {
    public ValuedListProperty(@NotNull String key, @NotNull ValueParser<List<V>> valueParser, List<V> defaultValue) {
        super(key, valueParser, defaultValue);
    }

    @Override
    public boolean isCommaSeparated() {
        return true;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }
}