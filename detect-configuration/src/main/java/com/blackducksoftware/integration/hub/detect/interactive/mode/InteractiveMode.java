/**
 * detect-configuration
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.interactive.mode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveOption;
import com.blackducksoftware.integration.hub.detect.interactive.reader.InteractiveReader;

public abstract class InteractiveMode {
    private final Map<DetectProperty, InteractiveOption> propertyToOptionMap = new HashMap<>();
    private PrintStream printStream;
    private InteractiveReader interactiveReader;
    private String profileName = null;

    public void init(final PrintStream printStream, final InteractiveReader reader) {
        this.printStream = printStream;
        this.interactiveReader = reader;
    }

    public abstract void configure();

    public String askQuestion(final String question) {
        printStream.println(question);
        return interactiveReader.readLine();
    }

    public String askSecretQuestion(final String question) {
        printStream.println(question);
        return interactiveReader.readPassword().toString();
    }

    public void setPropertyFromQuestion(final DetectProperty detectProperty, final String question) {
        final String value = askQuestion(question);
        setProperty(detectProperty, value);
    }

    public void setPropertyFromSecretQuestion(final DetectProperty detectProperty, final String question) {
        final String value = askSecretQuestion(question);
        setProperty(detectProperty, value);
    }

    public void setProperty(final DetectProperty detectProperty, final String value) {
        final InteractiveOption option;
        if (!propertyToOptionMap.containsKey(detectProperty)) {
            option = new InteractiveOption();
            option.setDetectProperty(detectProperty);
            propertyToOptionMap.put(detectProperty, option);
        } else {
            option = propertyToOptionMap.get(detectProperty);
        }
        option.setInteractiveValue(value);
    }

    public Boolean askYesOrNo(final String question) {
        return askYesOrNoWithMessage(question, null);
    }

    public Boolean askYesOrNoWithMessage(final String question, final String message) {
        printStream.print(question);
        if (StringUtils.isNotBlank(message)) {
            printStream.print(message);
        }
        printStream.print(" (Y|n)");
        printStream.println();
        final int maxAttempts = 3;
        int attempts = 0;
        while (attempts < maxAttempts) {
            final String response = interactiveReader.readLine();
            if (anyEquals(response, "y", "yes")) {
                return true;
            } else if (anyEquals(response, "n", "no")) {
                return false;
            }
            attempts += 1;
            printStream.println("Please answer yes or no.");
        }
        return null;
    }

    public Properties optionsToProperties() {
        final Properties properties = new Properties();
        for (final InteractiveOption interactiveOption : propertyToOptionMap.values()) {
            properties.put(interactiveOption.getDetectProperty().getPropertyName(), interactiveOption.getInteractiveValue());
        }

        return properties;
    }

    public boolean hasValueForField(final DetectProperty field) {
        return propertyToOptionMap.containsKey(field);
    }

    public void performStandardOutflow() {
        printSuccess();
        askToSave();
        readyToStartDetect();
    }

    public void readyToStartDetect() {
        printStream.println();
        printStream.println("Ready to start detect. Hit enter to proceed.");
        interactiveReader.readLine();
    }

    public void printSuccess() {
        printStream.println("Interactive Mode Succesfull!");
        printStream.println();
    }

    public void printProfile() {
        if (profileName != null) {
            printStream.println();
            printStream.println("In the future, to use this profile add the following option:");
            printStream.println();
            printStream.println("--spring.profiles.active=" + profileName);
        }
    }

    public void askToSave() {
        final Boolean saveSettings = askYesOrNo("Would you like to save these settings to an application.properties file?");
        if (saveSettings) {
            final Boolean customName = askYesOrNo("Would you like save these settings to a profile?");
            if (customName) {
                profileName = askQuestion("What is the profile name?");
            }

            saveOptionsToApplicationProperties();

            printProfile();
        }

    }

    public void printOptions() {
        for (final InteractiveOption interactiveOption : propertyToOptionMap.values()) {
            String fieldValue = interactiveOption.getInteractiveValue();
            final String propertyName = interactiveOption.getDetectProperty().getPropertyName().toLowerCase();
            if (propertyName.contains("password") || propertyName.contains("api.token")) {
                fieldValue = "";
                for (int i = 0; i < interactiveOption.getInteractiveValue().length(); i++) {
                    fieldValue += "*";
                }
            }
            printStream.println("--" + interactiveOption.getDetectProperty().getPropertyName() + "=" + fieldValue);
        }
    }

    public void saveOptionsToApplicationProperties() {
        final Properties properties = optionsToProperties();
        final File directory = new File(System.getProperty("user.dir"));
        String fileName = "application.properties";
        if (profileName != null) {
            fileName = "application-" + profileName + ".properties";
        }

        final File applicationsProperty = new File(directory, fileName);
        final OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(applicationsProperty);
            properties.store(outputStream, "Automatically generated during Detect Interactive Mode.");
            printStream.println();
            printStream.println("Succesfully saved to '" + applicationsProperty.getCanonicalPath() + "'!");
            outputStream.close();
        } catch (final IOException e) {
            printStream.println(e);
            printStream.println("Failed to write to application.properties.");
            throw new RuntimeException(e);
        }
    }

    public void printWelcome() {
        printStream.println("***** Welcome to Detect Interactive Mode *****");
        printStream.println("");
    }

    public void print(final String x) {
        printStream.print(x);
    }

    public void println(final String x) {
        printStream.println(x);
    }

    private boolean anyEquals(final String response, final String... options) {
        final String trimmed = response.trim().toLowerCase();
        for (final String opt : options) {
            if (trimmed.equals(opt)) {
                return true;
            }
        }
        return false;
    }

    public List<InteractiveOption> getInteractiveOptions() {
        return new ArrayList<>(propertyToOptionMap.values());
    }

}
