package com.synopsys.integration.detect;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.base.NullableProperty;
import com.synopsys.integration.configuration.property.base.ValuedProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.list.AllEnumList;
import com.synopsys.integration.configuration.property.types.enumallnone.list.AllNoneEnumList;
import com.synopsys.integration.configuration.property.types.enumallnone.list.NoneEnumList;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.property.AllNoneEnumListProperty;
import com.synopsys.integration.configuration.property.types.enumallnone.property.NoneEnumListProperty;

public class PropertyConfigUtils {
    /**
     * Will get the first property in a list that was provided by the user.
     */
    @SafeVarargs
    public static <V, R> Optional<R> getFirstProvidedValueOrEmpty(PropertyConfiguration propertyConfiguration, NullableProperty<V, R>... properties) {
        for (NullableProperty<V, R> property : properties) {
            if (propertyConfiguration.wasPropertyProvided(property)) {
                return propertyConfiguration.getValueOrEmpty(property);
            }
        }

        return Optional.empty();
    }

    /**
     * Will get the first property in a list that was provided by the user.
     * If no property was provided, the default value of the first property will be used.
     */
    @SafeVarargs
    public static <V, R> R getFirstProvidedValueOrDefault(@NotNull PropertyConfiguration propertyConfiguration, @NotNull ValuedProperty<V, R>... properties) {
        Optional<R> value = PropertyConfigUtils.getFirstProvidedValueOrEmpty(propertyConfiguration, properties);
        return value.orElseGet(() -> properties[0].convertValue(properties[0].getDefaultValue()));

    }

    /**
     * Will get the first property in a list that was provided by the user.
     * If no property was provided, the default will NOT be used.
     */
    @SafeVarargs
    public static <V, R> Optional<R> getFirstProvidedValueOrEmpty(@NotNull PropertyConfiguration propertyConfiguration, @NotNull ValuedProperty<V, R>... properties) {
        for (ValuedProperty<V, R> property : properties) {
            if (propertyConfiguration.wasPropertyProvided(property)) {
                return Optional.of(propertyConfiguration.getValueOrDefault(property));
            }
        }

        return Optional.empty();
    }
}