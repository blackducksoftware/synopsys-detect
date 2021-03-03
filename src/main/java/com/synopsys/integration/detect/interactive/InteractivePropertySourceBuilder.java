/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.interactive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.detect.configuration.DetectProperty;

public class InteractivePropertySourceBuilder {
    private final Map<Property, InteractiveOption> propertyToOptionMap = new HashMap<>();
    private final InteractiveWriter interactiveWriter;
    public static final String PROPERTY_SOURCE_NAME = "interactive";

    public InteractivePropertySourceBuilder(InteractiveWriter interactiveWriter) {
        this.interactiveWriter = interactiveWriter;
    }

    public <T extends Property> void setPropertyFromQuestion(DetectProperty<T> detectProperty, String question) {
        String value = interactiveWriter.askQuestion(question);
        setProperty(detectProperty, value);
    }

    public <T extends Property> void setPropertyFromSecretQuestion(DetectProperty<T> detectProperty, String question) {
        String value = interactiveWriter.askSecretQuestion(question);
        setProperty(detectProperty, value);
    }

    public <T extends Property> void setProperty(DetectProperty<T> detectProperty, String value) {
        InteractiveOption option;
        T propertyToSet = detectProperty.getProperty();
        if (!propertyToOptionMap.containsKey(propertyToSet)) {
            option = new InteractiveOption();
            option.setDetectProperty(propertyToSet);
            propertyToOptionMap.put(propertyToSet, option);
        } else {
            option = propertyToOptionMap.get(propertyToSet);
        }
        option.setInteractiveValue(value);
    }

    public Properties optionsToProperties() {
        Properties properties = new Properties();
        for (InteractiveOption interactiveOption : propertyToOptionMap.values()) {
            properties.put(interactiveOption.getDetectProperty().getKey(), interactiveOption.getInteractiveValue());
        }

        return properties;
    }

    public void saveToApplicationProperties() {
        saveToApplicationProperties(null);
    }

    public void saveToApplicationProperties(String profileName) {
        Properties properties = optionsToProperties();
        File directory = new File(System.getProperty("user.dir"));
        String fileName = "application.properties";
        if (profileName != null) {
            fileName = "application-" + profileName + ".properties";
        }

        File applicationsProperty = new File(directory, fileName);
        try (OutputStream outputStream = new FileOutputStream(applicationsProperty)) {
            properties.store(outputStream, "Automatically generated during Detect Interactive Mode.");
            interactiveWriter.println();
            interactiveWriter.println("Successfully saved to '" + applicationsProperty.getCanonicalPath() + "'!");
        } catch (IOException e) {
            interactiveWriter.println(e);
            interactiveWriter.println("Failed to write to application.properties.");
            throw new RuntimeException(e);
        }
    }

    public MapPropertySource build() {
        Map<String, String> interactivePropertyMap = propertyToOptionMap.values().stream()
                                                         .collect(Collectors.toMap(option -> option.getDetectProperty().getKey(), InteractiveOption::getInteractiveValue));
        return new MapPropertySource(PROPERTY_SOURCE_NAME, interactivePropertyMap);
    }

}
