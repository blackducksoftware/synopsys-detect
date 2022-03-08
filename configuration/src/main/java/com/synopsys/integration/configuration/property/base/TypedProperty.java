package com.synopsys.integration.configuration.property.base;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.parse.ValueParser;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueInfo;
import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;

/**
 * A property that is associated with a single type.
 *
 * It can't itself be retrieved from a Configuration but provides a shared parent class
 * for Nullable and Valued properties, both of which need a property with a single type.
 * @param <R> the type this property returns when it is retrieved from a Configuration.
 * @param <V> the type this property parses.
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

    @NotNull
    private final List<DeprecatedValueInfo> deprecatedValues = new ArrayList<>();

    @NotNull
    public List<DeprecatedValueUsage> checkForDeprecatedValues(V value) {
        return new ArrayList<>();// By default, a typed property does not have any deprecated values.
    }
}
