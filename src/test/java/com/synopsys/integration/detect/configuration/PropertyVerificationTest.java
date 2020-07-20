package com.synopsys.integration.detect.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.PropertyGroupInfo;
import com.synopsys.integration.configuration.property.PropertyHelpInfo;
import com.synopsys.integration.configuration.util.Category;

public class PropertyVerificationTest {

    @Test
    public void verifyProperties() throws IllegalAccessException {
        Set<String> missing = new HashSet<>();
        List<Property> kotlinProperties = Collections.emptyList(); // DetectProperties.Properties();

        List<DetectProperty<?>> javaProperties = collectJavaProperties();

        for (Property property : kotlinProperties) {
            if (!containsProperty(property, javaProperties)) {
                missing.add(property.getName());
            }
        }
        for (DetectProperty<?> property : javaProperties) {
            if (!containsProperty(property, kotlinProperties)) {
                missing.add(property.getName());
            }
        }

        Assertions.assertEquals(2, missing.size());
    }

    private boolean containsProperty(DetectProperty<?> detectProperty, List<Property> propertyList) {
        for (Property current : propertyList) {
            if (current.getName().equals(detectProperty.getName())
                    && (current.getFromVersion().equals(detectProperty.getFromVersion()))
                    && (equalPropertyHelpInfo(detectProperty, current))
                    && (equalPropertyGroupInfo(detectProperty, current))
                    //&& (equalCategory(detectProperty, current)) // TODO
                    && (equalPropertyDeprecationInfo(detectProperty, current))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsProperty(Property property, List<DetectProperty<?>> propertyList) {
        for (DetectProperty<?> current : propertyList) {
            if (current.getName().equals(property.getName())
                    && (current.getFromVersion().equals(property.getFromVersion()))
                    && (equalPropertyHelpInfo(current, property))
                    && (equalPropertyGroupInfo(current, property))
                    //&& (equalCategory(current, property)) // TODO
                    && (equalPropertyDeprecationInfo(current, property))) {
                return true;
            }
        }
        return false;
    }

    private boolean equalPropertyHelpInfo(DetectProperty<?> p1, Property p2) {
        PropertyHelpInfo info1 = p1.getPropertyHelpInfo();
        PropertyHelpInfo info2 = p2.getPropertyHelpInfo();
        if (info1 == null && info2 == null) {
            return true;
        }
        if (info1 == null || info2 == null) {
            return false;
        }

        if (info1.getShortText().equals(info2.getShortText())) {
            return (info1.getLongText() == null && info2.getLongText() == null) || info1.getLongText().equals(info2.getLongText());
        }
        return false;
    }

    private boolean equalPropertyGroupInfo(DetectProperty<?> p1, Property p2) {
        PropertyGroupInfo info1 = p1.getPropertyGroupInfo();
        PropertyGroupInfo info2 = p2.getPropertyGroupInfo();
        if (info1 == null && info2 == null) {
            return true;
        }
        if (info1 == null || info2 == null) {
            return false;
        }

        return info1.getPrimaryGroup().equals(info2.getPrimaryGroup()) &&
                   info1.getAdditionalGroups().containsAll(info2.getAdditionalGroups()) &&
                   info2.getAdditionalGroups().containsAll(info1.getAdditionalGroups());
    }

    private boolean equalCategory(DetectProperty<?> p1, Property p2) {
        Category info1 = p1.getCategory();
        Category info2 = p2.getCategory();
        if (info1 == null && info2 == null) {
            return true;
        }
        if (info1 == null || info2 == null) {
            return false;
        }
        return info1.getName().equals(info2.getName());
    }

    private boolean equalPropertyDeprecationInfo(DetectProperty<?> p1, Property p2) {
        PropertyDeprecationInfo info1 = p1.getPropertyDeprecationInfo();
        PropertyDeprecationInfo info2 = p2.getPropertyDeprecationInfo();
        if (info1 == null && info2 == null) {
            return true;
        }
        if (info1 == null || info2 == null) {
            return false;
        }

        return info1.getDescription().equals(info2.getDescription()) &&
                   info1.getDeprecationText().equals(info2.getDeprecationText()) &&
                   info1.getFailInVersion().getIntValue().equals(info2.getFailInVersion().getIntValue()) &&
                   info1.getRemoveInVersion().getIntValue().equals(info2.getRemoveInVersion().getIntValue());
    }

    private List<DetectProperty<?>> collectJavaProperties() throws IllegalAccessException {
        List<DetectProperty<?>> properties = new ArrayList<>();
        Field[] allFields = DetectProperties.class.getDeclaredFields();
        for (Field field : allFields) {
            if (!field.getType().equals(String.class)) {
                Object property = field.get(DetectProperty.class);
                properties.add((DetectProperty<?>) property);
            }
        }
        return properties;
    }
}
