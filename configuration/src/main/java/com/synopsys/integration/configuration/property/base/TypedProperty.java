package com.synopsys.integration.configuration.property.base;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.Property;

/**
 * A property that is associated with a single type.
 *
 * It can't itself be retrieved from a Configuration but provides a shared parent class
 * for Nullable and Valued properties, both of which need a property with a single type.
 * @param <T> the type this property returns when it is retrieved from a Configuration.
 */
public abstract class TypedProperty<V, R> extends Property { //where V is the underlying value type and R is the type returned by the configuration.
    @NotNull
    private final ValueParser<V> valueParser;

    public TypedProperty(@NotNull String key, @NotNull ValueParser<V> valueParser) {
        super(key);
        this.valueParser = valueParser;
    }

    @NotNull
    public ValueParser<V> getValueParser() {
        return valueParser;
    }

    @NotNull
    public abstract R convertValue(V value);
}
