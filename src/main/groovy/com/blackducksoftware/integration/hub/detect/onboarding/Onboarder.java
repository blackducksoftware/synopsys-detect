/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.detect.onboarding;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.ReflectionUtils;
import com.blackducksoftware.integration.hub.detect.help.SpringValueUtils;

public class Onboarder {

    public Onboarder(final PrintStream printStream, final Scanner scanner, final DetectConfiguration detectConfiguration) {
        this.printStream = printStream;
        this.scanner = scanner;
        this.detectConfiguration = detectConfiguration;
    }

    private final DetectConfiguration detectConfiguration;
    private final PrintStream printStream;
    private final Scanner scanner;
    private final Map<String, OnboardingOption> fieldOptions = new HashMap<>();

    public String askQuestion(final String question) {
        printStream.println(question);
        return scanner.nextLine();
    }

    public void askFieldQuestion(final String field, final String question) {
        setField(field, askQuestion(question));
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
            final String response = scanner.nextLine();
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
        scanner.nextLine();
        scanner.close();
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
            printStream.println("--" + opt.springKey + "=" + opt.onboardingValue);
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
}
