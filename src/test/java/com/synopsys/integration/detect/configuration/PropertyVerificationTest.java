package com.synopsys.integration.detect.configuration;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.configuration.property.PropertyGroupInfo;
import com.synopsys.integration.configuration.property.PropertyHelpInfo;
import com.synopsys.integration.configuration.util.Category;
import com.synopsys.integration.configuration.util.Group;

public class PropertyVerificationTest {

    @Test
    public void verifyProperties() throws IllegalAccessException {
        Set<String> missing = new HashSet<>();
        List<Property> kotlinProperties = DetectPropertiesKotlin.Companion.getProperties();

        List<Property> javaProperties = collectJavaProperties();

        for (Property property : kotlinProperties) {
            if (!containsProperty(property, javaProperties)) {
                missing.add(property.getName());
            }
        }
        for (Property property : javaProperties) {
            if (!containsProperty(property, kotlinProperties)) {
                missing.add(property.getName());
            }
        }

        Assertions.assertTrue(missing.size() == 2);
    }

    private boolean containsProperty(Property property, List<Property> propertyList) {
        for (Property current : propertyList) {
            try {
                if (current.getName().equals(property.getName())
                        && (current.getFromVersion().equals(property.getFromVersion()))
                        && (equalPropertyHelpInfo(current, property))
                        && (equalPropertyGroupInfo(current, property))
                        && (equalCategory(current, property))
                        && (equalPropertyDeprecationInfo(current, property))) {
                    return true;
                }
            } catch (NullPointerException e) {
                System.out.println("");
            }
        }
        return false;
    }

    private boolean equalPropertyHelpInfo(Property p1, Property p2) {
        PropertyHelpInfo info1 = p1.getPropertyHelpInfo();
        PropertyHelpInfo info2 = p2.getPropertyHelpInfo();
        if (info1 == null && info2 == null) {
            return true;
        }
        if (info1 == null || info2 == null) {
            return false;
        }

        if (info1.getShortText().equals(info2.getShortText())) {
            return (info1.getLongText() == null && info2.getLongText() == null) || (info1.getLongText() != null && info1.getLongText().equals(info2.getLongText()));
        }
        return false;
    }

    private boolean equalPropertyGroupInfo(Property p1, Property p2) {
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

    private boolean equalCategory(Property p1, Property p2) {
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

    private boolean equalPropertyDeprecationInfo(Property p1, Property p2) {
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

    private List<Property> collectJavaProperties() throws IllegalAccessException {
        List<Property> properties = new ArrayList<>();
        Field[] allFields = DetectProperties.class.getDeclaredFields();
        for (Field field : allFields) {
            if (!field.getType().equals(String.class)) {
                Object property = field.get(Property.class);
                DetectProperty detectProperty = (DetectProperty) property;
                properties.add(convertDetectPropertyToProperty(detectProperty));
            }
        }
        return properties;
    }

    private Property convertDetectPropertyToProperty(DetectProperty detectProperty) {
        Property property =  detectProperty.getProperty();
        property.setInfo(detectProperty.getName(), detectProperty.getFromVersion());
        if (detectProperty.getPropertyHelpInfo() != null) {
            property.setHelp(detectProperty.getPropertyHelpInfo().getShortText(), detectProperty.getPropertyHelpInfo().getLongText());
        }
        if (detectProperty.getPropertyGroupInfo() != null) {
            property.setGroups(detectProperty.getPropertyGroupInfo().getPrimaryGroup(), (Group[]) detectProperty.getPropertyGroupInfo().getAdditionalGroups().toArray());
        }
        if (detectProperty.getCategory() == null) {
            property.setCategory(DetectCategory.Simple);
        } else {
            property.setCategory(detectProperty.getCategory());
        }
        if (detectProperty.getPropertyDeprecationInfo() != null) {
            property.setDeprecated(detectProperty.getPropertyDeprecationInfo().getDescription(), detectProperty.getPropertyDeprecationInfo().getFailInVersion(), detectProperty.getPropertyDeprecationInfo().getRemoveInVersion());
        }
        return property;
    }
}
