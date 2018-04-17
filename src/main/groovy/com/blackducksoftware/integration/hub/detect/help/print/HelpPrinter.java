/**
 * hub-detect
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
package com.blackducksoftware.integration.hub.detect.help.print;

import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.help.ArgumentState;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpPrinter {

    @Autowired
    private HelpOptionPrinter optionPrinter;

    @Autowired
    private HelpDetailedOptionPrinter detailPrinter;

    public void printAppropriateHelpMessage(final PrintStream printStream, final List<DetectOption> allOptions, final ArgumentState state) {
        final HelpTextWriter writer = new HelpTextWriter();

        final List<DetectOption> currentOptions = allOptions.stream().filter(it -> !it.getHelp().isDeprecated).collect(Collectors.toList());
        final List<DetectOption> deprecatedOptions = allOptions.stream().filter(it -> it.getHelp().isDeprecated).collect(Collectors.toList());
        final List<String> allPrintGroups = getPrintGroups(currentOptions);

        if (state.isVerboseHelpMessage) {
            printVerboseOptions(writer, currentOptions, null);
        } else if (state.isDeprecatedHelpMessage) {
            optionPrinter.printOptions(writer, deprecatedOptions, "Showing only deprecated properties.");
        } else {
            if (state.parsedValue != null) {
                if (isProperty(currentOptions, state.parsedValue)) {
                    printDetailedHelp(writer, allOptions, state.parsedValue);
                } else if (isPrintGroup(allPrintGroups, state.parsedValue)){
                    printHelpFilteredByPrintGroup(writer, currentOptions, state.parsedValue);
                } else {
                    printHelpFilteredBySearchTerm(writer, currentOptions, state.parsedValue);
                }
            }else {
                printDefaultHelp(writer, currentOptions);
            }
        }

        optionPrinter.printStandardFooter(writer, getPrintGroupText(allPrintGroups));

        writer.write(printStream);
    }

    private void printVerboseOptions(final HelpTextWriter writer, final List<DetectOption> options, final String notes) {
        final List<DetectOption> sorted = options.stream().sorted((o1, o2) -> {
            if (o1.getHelp().primaryGroup.equals(o2.getHelp().primaryGroup)) {
                return o1.getKey().compareTo(o2.getKey());
            }else {
                return o1.getHelp().primaryGroup.compareTo(o2.getHelp().primaryGroup);
            }
        }).collect(Collectors.toList());
        optionPrinter.printOptions(writer, sorted, notes);
    }

    private void printDetailedHelp(final HelpTextWriter writer, final List<DetectOption> options, final String optionName) {
        final DetectOption option = options.stream()
                .filter(it -> it.getKey().equals(optionName))
                .findFirst().orElse(null);

        if (option == null) {
            writer.println("Could not find option named: " + optionName);
        } else {
            detailPrinter.printDetailedOption(writer, option);
        }
    }

    private void printDefaultHelp(final HelpTextWriter writer, final List<DetectOption> options) {
        printHelpFilteredByPrintGroup(writer, options, DetectConfiguration.PRINT_GROUP_DEFAULT);
    }

    private void printHelpFilteredByPrintGroup(final HelpTextWriter writer, final List<DetectOption> options, final String filterGroup) {
        final String notes = "Showing help only for: " + filterGroup;

        final List<DetectOption> filteredOptions = options.stream()
                .filter(it -> it.getHelp().groups.stream().anyMatch(printGroup -> printGroup.equalsIgnoreCase(filterGroup)))
                .sorted((o1, o2) -> o1.getKey().compareTo(o2.getKey()))
                .collect(Collectors.toList());

        optionPrinter.printOptions(writer, filteredOptions, notes);
    }

    private void printHelpFilteredBySearchTerm(final HelpTextWriter writer, final List<DetectOption> options, final String searchTerm) {
        final String notes = "Showing help only for fields that contain: " + searchTerm;

        final List<DetectOption> filteredOptions = options.stream()
                .filter(it -> it.getKey().contains(searchTerm))
                .collect(Collectors.toList());

        optionPrinter.printOptions(writer, filteredOptions, notes);
    }

    private boolean isPrintGroup (final List<String> allPrintGroups, final String filterGroup) {
        return allPrintGroups.contains(filterGroup);
    }

    private boolean isProperty (final List<DetectOption> allOptions, final String filterTerm) {
        return allOptions.stream()
                .map(it -> it.getKey())
                .anyMatch(it -> it.equals(filterTerm));
    }

    private List<String> getPrintGroups(final List<DetectOption> options) {
        return options.stream()
                .flatMap(it -> it.getHelp().groups.stream())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    private String getPrintGroupText(final List<String> printGroups) {
        return printGroups.stream().collect(Collectors.joining(","));
    }

}
