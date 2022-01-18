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
    public static <B extends Enum<B>> NoneEnumList<B> getNoneList(PropertyConfiguration propertyConfiguration, NoneEnumListProperty<B> property) {
        return property.toList(propertyConfiguration.getValue(property));
    }

    public static <B extends Enum<B>> AllNoneEnumList<B> getAllNoneList(PropertyConfiguration propertyConfiguration, AllNoneEnumListProperty<B> property) {
        return property.toList(propertyConfiguration.getValue(property));
    }

    public static <B extends Enum<B>> AllEnumList<B> getAllList(PropertyConfiguration propertyConfiguration, AllEnumListProperty<B> property) {
        return property.toList(propertyConfiguration.getValue(property));
    }

    /**
     * Will get the first property in a list that was provided by the user.
     */
    public static <T> Optional<T> getFirstProvidedValueOrEmpty(PropertyConfiguration propertyConfiguration, NullableProperty<T>... properties) {
        for (NullableProperty<T> property : properties) {
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
    public static <T> T getFirstProvidedValueOrDefault(@NotNull PropertyConfiguration propertyConfiguration, @NotNull ValuedProperty<T>... properties) {
        Optional<T> value = PropertyConfigUtils.getFirstProvidedValueOrEmpty(propertyConfiguration, properties);
        return value.orElseGet(() -> properties[0].getDefaultValue());

    }

    /**
     * Will get the first property in a list that was provided by the user.
     * If no property was provided, the default will NOT be used.
     */
    public static <T> Optional<T> getFirstProvidedValueOrEmpty(@NotNull PropertyConfiguration propertyConfiguration, @NotNull ValuedProperty<T>... properties) {
        for (ValuedProperty<T> property : properties) {
            if (propertyConfiguration.wasPropertyProvided(property)) {
                return Optional.of(propertyConfiguration.getValueOrDefault(property));
            }
        }

        return Optional.empty();
    }
}