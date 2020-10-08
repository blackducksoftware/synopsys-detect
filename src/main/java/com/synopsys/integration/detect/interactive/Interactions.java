/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.detect.interactive;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.detect.interactive.reader.InteractiveReader;

public class Interactions {
    private final Map<Property, InteractiveOption> propertyToOptionMap = new HashMap<>();
    private final PrintStream printStream;
    private final InteractiveReader interactiveReader;
    private String profileName = null;

    public Interactions(PrintStream printStream, InteractiveReader reader) {
        this.printStream = printStream;
        this.interactiveReader = reader;
    }

    public String askQuestion(String question) {
        printStream.println(question);
        return interactiveReader.readLine();
    }

    public String askSecretQuestion(String question) {
        printStream.println(question);
        return interactiveReader.readPassword();
    }

    public void setPropertyFromQuestion(Property detectProperty, String question) {
        String value = askQuestion(question);
        setProperty(detectProperty, value);
    }

    public void setPropertyFromSecretQuestion(Property detectProperty, String question) {
        String value = askSecretQuestion(question);
        setProperty(detectProperty, value);
    }

    public void setProperty(Property detectProperty, String value) {
        InteractiveOption option;
        if (!propertyToOptionMap.containsKey(detectProperty)) {
            option = new InteractiveOption();
            option.setDetectProperty(detectProperty);
            propertyToOptionMap.put(detectProperty, option);
        } else {
            option = propertyToOptionMap.get(detectProperty);
        }
        option.setInteractiveValue(value);
    }

    public Boolean askYesOrNo(String question) {
        return askYesOrNoWithMessage(question, null);
    }

    public Boolean askYesOrNoWithMessage(String question, String message) {
        printStream.print(question);
        if (StringUtils.isNotBlank(message)) {
            printStream.print(message);
        }
        printStream.print(" (Y|n)");
        printStream.println();
        final int maxAttempts = 3;
        int attempts = 0;
        while (attempts < maxAttempts) {
            String response = interactiveReader.readLine();
            if (anyEquals(response, "y", "yes")) {
                return true;
            } else if (anyEquals(response, "n", "no")) {
                return false;
            }
            attempts += 1;
            printStream.println("Please answer yes or no.");
        }
        return false;
    }

    public Properties optionsToProperties() {
        Properties properties = new Properties();
        for (InteractiveOption interactiveOption : propertyToOptionMap.values()) {
            properties.put(interactiveOption.getDetectProperty().getKey(), interactiveOption.getInteractiveValue());
        }

        return properties;
    }

    public boolean hasValueForField(Property field) {
        return propertyToOptionMap.containsKey(field);
    }

    public void saveAndEndInteractiveMode() {
        printSuccess();
        askToSave();
        printReadyToStartDetect();
    }

    public void printReadyToStartDetect() {
        printStream.println();
        printStream.println("Ready to start Detect. Hit enter to proceed.");
        interactiveReader.readLine();
    }

    public void printSuccess() {
        printStream.println("Interactive Mode Successful!");
        printStream.println();
    }

    public void askToSave() {
        Boolean saveSettings = askYesOrNo("Would you like to save these settings to an application.properties file?");
        if (saveSettings) {
            Boolean customName = askYesOrNo("Would you like save these settings to a profile?");
            if (customName) {
                profileName = askQuestion("What is the profile name?");
            }

            saveOptionsToApplicationProperties();

            if (profileName != null) {
                printStream.println();
                printStream.println("In the future, to use this profile add the following option:");
                printStream.println();
                printStream.println("--spring.profiles.active=" + profileName);
            }
        }
    }

    public void saveOptionsToApplicationProperties() {
        Properties properties = optionsToProperties();
        File directory = new File(System.getProperty("user.dir"));
        String fileName = "application.properties";
        if (profileName != null) {
            fileName = "application-" + profileName + ".properties";
        }

        File applicationsProperty = new File(directory, fileName);
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(applicationsProperty);
            properties.store(outputStream, "Automatically generated during Detect Interactive Mode.");
            printStream.println();
            printStream.println("Successfully saved to '" + applicationsProperty.getCanonicalPath() + "'!");
            outputStream.close();
        } catch (IOException e) {
            printStream.println(e);
            printStream.println("Failed to write to application.properties.");
            throw new RuntimeException(e);
        }
    }

    public void printWelcome() {
        printStream.println("***** Welcome to Detect Interactive Mode *****");
        printStream.println();
    }

    public void println(String x) {
        printStream.println(x);
    }

    private boolean anyEquals(String response, String... options) {
        String trimmed = response.trim().toLowerCase();
        for (String opt : options) {
            if (trimmed.equals(opt)) {
                return true;
            }
        }
        return false;
    }

    public MapPropertySource createPropertySource() {
        Map<String, String> interactivePropertyMap = propertyToOptionMap.values().stream()
                                                         .collect(Collectors.toMap(option -> option.getDetectProperty().getKey(), InteractiveOption::getInteractiveValue));
        return new MapPropertySource("interactive", interactivePropertyMap);
    }

}
