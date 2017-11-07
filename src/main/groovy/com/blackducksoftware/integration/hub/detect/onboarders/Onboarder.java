/**
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.onboarders;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.onboarding.OnboardingOption;
import com.blackducksoftware.integration.hub.detect.onboarding.reader.OnboardingReader;
import com.blackducksoftware.integration.hub.detect.util.SpringValueUtils;

public abstract class Onboarder {

    private PrintStream printStream;
    private OnboardingReader onboardingReader;
    private final Map<String, OnboardingOption> propertyToOptionMap = new HashMap<>();
    private String profileName = null;

    public void init(final PrintStream printStream, final OnboardingReader reader) {
        this.printStream = printStream;
        this.onboardingReader = reader;
    }

    public void onboard() {

    }

    public String askQuestion(final String question) {
        printStream.println(question);
        return onboardingReader.readLine();
    }

    public String askSecretQuestion(final String question) {
        printStream.println(question);
        return onboardingReader.readPassword().toString();
    }

    public void setPropertyFromQuestion(final String propertyName, final String question) {
        final String value = askQuestion(question);
        setProperty(propertyName, value);
    }

    public void setPropertyFromSecretQuestion(final String propertyName, final String question) {
        final String value = askSecretQuestion(question);
        setProperty(propertyName, value);
    }

    public void setProperty(final String propertyName, final String value) {
        OnboardingOption option;
        if (!propertyToOptionMap.containsKey(propertyName)) {
            option = new OnboardingOption();
            option.fieldName = propertyName;
            option.springKey = springKeyFromFieldName(propertyName);
            propertyToOptionMap.put(propertyName, option);
        } else {
            option = propertyToOptionMap.get(propertyName);
        }
        option.onboardingValue = value;
    }

    public Boolean askYesOrNo(final String question) {
        printStream.print(question);
        printStream.print(" (Y|n)");
        printStream.println();
        final int maxAttempts = 3;
        int attempts = 0;
        while (attempts < maxAttempts) {
            final String response = onboardingReader.readLine();
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

    private String springKeyFromFieldName(final String fieldName) {
        try {
            final Field field = DetectConfiguration.class.getDeclaredField(fieldName);

            final Value valueAnnotation = field.getAnnotation(Value.class);
            final String key = SpringValueUtils.springKeyFromValueAnnotation(valueAnnotation.value());
            return key;
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (final SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> optionsToSpringKeys() {
        final Map<String, String> springKeyMap = new HashMap<>();
        for (final OnboardingOption onboardingOption : propertyToOptionMap.values()) {
            springKeyMap.put(onboardingOption.springKey, onboardingOption.onboardingValue);
        }

        return springKeyMap;
    }

    public Properties optionsToProperties() {
        final Properties properties = new Properties();
        for (final OnboardingOption onboardingOption : propertyToOptionMap.values()) {
            properties.put(onboardingOption.springKey, onboardingOption.onboardingValue);
        }

        return properties;
    }

    public boolean hasValueForField(final String field) {
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
        onboardingReader.readLine();
    }

    public void printSuccess() {
        printStream.println("Onboarding succesfull!");
        printStream.println();
        printStream.println("In the future, start detect with the following options:");
        printStream.println();
        printOptions();
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
        for (final OnboardingOption onboardingOption : propertyToOptionMap.values()) {
            String fieldValue = onboardingOption.onboardingValue;
            if (onboardingOption.fieldName.toLowerCase().contains("password")) {
                fieldValue = "";
                for (int i = 0; i < onboardingOption.onboardingValue.length(); i++) {
                    fieldValue += "*";
                }
            }
            printStream.println("--" + onboardingOption.springKey + "=" + fieldValue);
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
        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(applicationsProperty);
            properties.store(outputStream, "Automatically generated during Detect Onboarding.");
            printStream.println();
            printStream.println("Succesfully saved to '" + applicationsProperty.getCanonicalPath() + "'!");
            outputStream.close();
        } catch (final FileNotFoundException e) {
            printStream.println(e);
            printStream.println("Failed to write to application.properties.");
            throw new RuntimeException(e);
        } catch (final IOException e) {
            printStream.println(e);
            printStream.println("Failed to write to application.properties.");
            throw new RuntimeException(e);
        }
    }

    public void printWelcome() {
        printStream.println("***** Welcome to Detect Onboarding *****");
        printStream.println("");
    }

    public void print(final String x) {
        printStream.print(x);
    }

    public void println(final String x) {
        printStream.println(x);
    }

    private boolean anyEquals(final String response, final String... options) {
        final String trimmed = response.trim();
        for (final String opt : options) {
            if (trimmed.equals(opt)) {
                return true;
            }
        }
        return false;
    }

    public List<OnboardingOption> getOnboardedOptions() {
        return new ArrayList<>(propertyToOptionMap.values());
    }
}
