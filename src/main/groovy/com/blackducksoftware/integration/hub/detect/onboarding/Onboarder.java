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
package com.blackducksoftware.integration.hub.detect.onboarding;

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
import com.blackducksoftware.integration.hub.detect.help.ReflectionUtils;
import com.blackducksoftware.integration.hub.detect.help.SpringValueUtils;

public class Onboarder {

    public Onboarder(final PrintStream printStream, final OnboardingReader reader, final DetectConfiguration detectConfiguration) {
        this.printStream = printStream;
        this.reader = reader;
        this.detectConfiguration = detectConfiguration;
    }

    private final DetectConfiguration detectConfiguration;
    private final PrintStream printStream;
    private final OnboardingReader reader;
    private final Map<String, OnboardingOption> fieldOptions = new HashMap<>();

    public String askQuestion(final String question) {
        printStream.println(question);
        return reader.readLine();
    }

    public String askSecretQuestion(final String question) {
        printStream.println(question);
        return reader.readPassword().toString();
    }

    public void askFieldQuestion(final String field, final String question) {
        setField(field, askQuestion(question));
    }

    public void askSecretFieldQuestion(final String field, final String question) {
        setField(field, askSecretQuestion(question));
    }

    public void setField(final String field, final String value) {
        OnboardingOption option;
        if (!fieldOptions.containsKey(field)) {
            option = new OnboardingOption();
            option.fieldName = field;
            option.springKey = springKeyFromFieldName(field);
            fieldOptions.put(field, option);
        } else {
            option = fieldOptions.get(field);
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
            final String response = reader.readLine();
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
            final Field field = detectConfiguration.getClass().getDeclaredField(fieldName);

            final Value valueAnnotation = field.getAnnotation(Value.class);
            final String key = SpringValueUtils.springKeyFromValueAnnotation(valueAnnotation.value());
            return key;
        } catch (final NoSuchFieldException e) {

        } catch (final SecurityException e) {

        }
        return null;
    }

    public Map<String, String> optionsToSpringKeys() {
        final Map<String, String> springKeyMap = new HashMap<>();
        for (final OnboardingOption opt : fieldOptions.values()) {
            springKeyMap.put(opt.springKey, opt.onboardingValue);
        }

        return springKeyMap;
    }

    public Properties optionsToProperties() {
        final Properties properties = new Properties();
        for (final OnboardingOption opt : fieldOptions.values()) {
            properties.put(opt.springKey, opt.onboardingValue);
        }

        return properties;
    }

    public boolean hasValueForField(final String field) {
        return fieldOptions.containsKey(field);
    }

    public void saveOptionsToConfiguration() {
        for (final OnboardingOption opt : fieldOptions.values()) {

            Field field;
            try {
                field = detectConfiguration.getClass().getDeclaredField(opt.fieldName);
            } catch (final NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (final SecurityException e) {
                throw new RuntimeException(e);
            }
            field.setAccessible(true);
            ReflectionUtils.setValue(field, detectConfiguration, opt.onboardingValue);

        }
    }

    public void performStandardOutflow() {
        printSuccess();
        askToSave();
        saveOptionsToConfiguration();
        readyToStartDetect();
    }

    public void readyToStartDetect() {
        printStream.println();
        printStream.println("Ready to start detect. Hit enter to proceed.");
        reader.readLine();
    }

    public void printSuccess() {
        printStream.println("Onboarding succesfull!");
        printStream.println();
        printStream.println("In the future, start detect with the following options:");
        printStream.println();
        printOptions();
        printStream.println();
    }

    public void askToSave() {
        final Boolean saveSettings = askYesOrNo("Would you like to save these settings to an application.properties file?");
        if (saveSettings) {
            saveOptionsToApplicationProperties();
        }
    }

    public void printOptions() {
        for (final OnboardingOption opt : fieldOptions.values()) {
            String fieldValue = opt.onboardingValue;
            if (opt.fieldName.toLowerCase().contains("password")) {
                fieldValue = "";
                for (int i = 0; i < opt.onboardingValue.length(); i++) {
                    fieldValue += "*";
                }
            }
            printStream.println("--" + opt.springKey + "=" + fieldValue);
        }
    }

    public void saveOptionsToApplicationProperties() {
        final Properties properties = optionsToProperties();
        final File directory = new File(System.getProperty("user.dir"));
        final File applicationsProperty = new File(directory, "application.properties");
        OutputStream outs;
        try {
            outs = new FileOutputStream(applicationsProperty);
            properties.store(outs, "Automatically generated during Detect Onboarding.");
            printStream.println();
            printStream.println("Succesfully saved to '" + applicationsProperty.getCanonicalPath() + "'!");
            outs.close();
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
        return new ArrayList<>(fieldOptions.values());
    }
}
