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
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Scanner;

import org.springframework.beans.factory.annotation.Value;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.ReflectionUtils;
import com.blackducksoftware.integration.hub.detect.help.SpringValueUtils;
import com.blackducksoftware.integration.hub.detect.help.ValueDescriptionManager;

public class Onboarder {

    public Onboarder(final DetectConfiguration detectConfiguration, final ValueDescriptionManager valueDescriptionAnnotationFinder) {
        this.detectConfiguration = detectConfiguration;
        this.valueDescriptionAnnotationFinder = valueDescriptionAnnotationFinder;
    }

    public DetectConfiguration detectConfiguration;
    public ValueDescriptionManager valueDescriptionAnnotationFinder;

    public void onboard(final PrintStream printStream) throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        final Scanner scanner = new Scanner(System.in);

        printStream.println("Onboarding flag found. Starting onboarding process.");

        final Map<String, String> options = new HashMap<>();
        final Boolean hubConnect = askYesOrNo(scanner, printStream, "Would you like to connect to a Hub Instance?");
        if (hubConnect == true) {
            options.put("hubUrl", askQuestion(scanner, printStream, "What is the hub instance url?"));
            options.put("hubUsername", askQuestion(scanner, printStream, "What is the hub username?"));
            options.put("hubPassword", askQuestion(scanner, printStream, "What is the hub password?"));
            printStream.println("Hub information updated.");

            final Boolean useProxy = askYesOrNo(scanner, printStream, "Would you like to configure a proxy for the hub?");
            if (useProxy) {
                options.put("hubProxyHost", askQuestion(scanner, printStream, "What is the hub proxy host?"));
                options.put("hubProxyPort", askQuestion(scanner, printStream, "What is the hub proxy port?"));
                options.put("hubProxyUsername", askQuestion(scanner, printStream, "What is the hub proxy username?"));
                options.put("hubProxyPassword", askQuestion(scanner, printStream, "What is the hub proxy password?"));
                printStream.println("Hub proxy updated.");
            }

            final Boolean trustCert = askYesOrNo(scanner, printStream, "Would you like to automatically trust the hub certificate?");
            if (trustCert) {
                options.put("hubTrustCertificate", "true");
                printStream.println("Hub certificate updated.");
            }

            final Boolean hubName = askYesOrNo(scanner, printStream, "Would you like to provide a project name and version to use on the hub?");
            if (hubName) {
                options.put("projectName", askQuestion(scanner, printStream, "What is the hub project name?"));
                options.put("projectVersionName", askQuestion(scanner, printStream, "What is the hub project version?"));
                printStream.println("Project information updated.");
            }
        } else {
            printStream.println("Setting detect to offline mode.");
            options.put("hubOfflineMode", "true");
        }

        final Boolean scan = askYesOrNo(scanner, printStream, "Would you like run a CLI scan?");
        if (!scan) {
            printStream.println("Disabling signature scanner.");
            options.put("hubSignatureScannerDisabled", "true");
        } else if (scan && hubConnect) {
            final Boolean upload = askYesOrNo(scanner, printStream, "Would you like to upload CLI scan results to the hub?");
            if (!upload) {
                printStream.println("Setting signature scanner to dry run.");
                options.put("hubSignatureScannerDryRun", "true");
            }
        }

        if (scan) {
            final Boolean custom = askYesOrNo(scanner, printStream, "Would you like to provide a custom scanner?");
            if (custom) {
                final Boolean download = askYesOrNo(scanner, printStream, "Would you like to download the custom scanner?");
                if (download) {
                    options.put("hubSignatureScannerHostUrl", askQuestion(scanner, printStream, "What is the scanner host url?"));
                } else {
                    options.put("hubSignatureScannerOfflineLocalPath", askQuestion(scanner, printStream, "What is the location of your offline scanner?"));
                }
            }
        }

        printStream.println("Onboarding succesfull!");
        printStream.println();
        printStream.println("In the future, start detect with the following options:");
        printStream.println();

        final Properties properties = new Properties();
        for (final Entry<String, String> opt : options.entrySet()) {

            final Field field = detectConfiguration.getClass().getDeclaredField(opt.getKey());
            field.setAccessible(true);

            final Value valueAnnotation = field.getAnnotation(Value.class);
            final String key = SpringValueUtils.springKeyFromValueAnnotation(valueAnnotation.value());
            final String value = opt.getValue();
            printStream.println("--" + key + "=" + value);
            properties.put(key, value);
            ReflectionUtils.setValue(field, detectConfiguration, opt.getValue(), true);

        }

        printStream.println();

        final Boolean saveSettings = askYesOrNo(scanner, printStream, "Would you like to save these settings to an application.properties file?");
        if (saveSettings) {
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

        printStream.println();
        printStream.println("Ready to start detect. Hit enter to proceed.");
        scanner.nextLine();
        scanner.close();

    }

    public String askQuestion(final Scanner scanner, final PrintStream printStream, final String question) {
        printStream.println(question);
        return scanner.nextLine();
    }

    public Boolean askYesOrNo(final Scanner scanner, final PrintStream printStream, final String question) {
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
