package com.synopsys.integration.configuration.config;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.configuration.help.PropertyInfo;
import com.synopsys.integration.configuration.property.Property;

public class PropertyInfoCollector {
    private PropertyConfiguration propertyConfiguration;

    public PropertyInfoCollector(final PropertyConfiguration propertyConfiguration) {
        this.propertyConfiguration = propertyConfiguration;
    }

    public List<PropertyInfo> collectPropertyInfo(List<Property> knownProperties, Predicate<String> shouldMaskPropertyValue) {
        List<PropertyInfo> propertyValues = new LinkedList<>();
        for (Property property : knownProperties) {
            if (!propertyConfiguration.wasKeyProvided(property.getKey())) {
                continue;
            }

            String value = propertyConfiguration.getRaw(property).orElse("");
            String maskedValue = value;
            if (shouldMaskPropertyValue.test(value)) {
                maskedValue = StringUtils.repeat('*', maskedValue.length());
            }
            propertyValues.add(new PropertyInfo(property.getKey(), maskedValue, property));
        }
        return propertyValues;
    }

    public static Predicate<String> maskPasswordsAndTokensPredicate() {
        return propertyKey -> propertyKey.toLowerCase().contains("password") || propertyKey.toLowerCase().contains("api.token") || propertyKey.toLowerCase().contains("access.token");
    }
}
