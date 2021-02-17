/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
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
package com.synopsys.integration.detect.configuration.help.print;

import java.io.PrintStream;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.util.Group;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;

public class HelpPrinter {
    private static final String DIAGNOSTIC_HELP_TEXT = "\nDiagnostics mode:\n\n" +
                                                           "In diagnostics mode, Detect will produce a diagnostics zip file that contains a collection of intermediate and output files\n" +
                                                           "that can be very useful for troubleshooting. Extended diagnostics mode writes additional files to the diagnostics zip file.\n" +
                                                           "Invoke diagnostics mode by adding -d (diagnostics mode) or -de (extended diagnostics mode) to the command line.\n" +
                                                           "The path to the generated diagnostics file can be found in the log (look for: \"Diagnostics file created at: ...\").\n" +
                                                           "The diagnostics file can be large, so you may want to generate it only when you will actually use it.\n";

    private static final Comparator<Property> SORT_BY_GROUP_THEN_KEY = (o1, o2) -> {
        if (o1.getPropertyGroupInfo().getPrimaryGroup().getName().equals(o2.getPropertyGroupInfo().getPrimaryGroup().getName())) {
            return o1.getKey().compareTo(o2.getKey());
        } else {
            return o1.getPropertyGroupInfo().getPrimaryGroup().getName().compareTo(o2.getPropertyGroupInfo().getPrimaryGroup().getName());
        }
    };

    public void printAppropriateHelpMessage(final PrintStream printStream, final List<Property> allOptions, List<Group> allGroups, Group defaultGroup, final DetectArgumentState state) {
        final HelpTextWriter writer = new HelpTextWriter();

        final List<Property> currentOptions = allOptions.stream()
                                                  .filter(it -> it.getPropertyDeprecationInfo() == null)
                                                  .collect(Collectors.toList());
        final List<Property> deprecatedOptions = allOptions.stream()
                                                     .filter(it -> it.getPropertyDeprecationInfo() != null)
                                                     .collect(Collectors.toList());

        if (state.isVerboseHelp()) {
            printOptions(writer, currentOptions, null);
        } else if (state.isDeprecatedHelp()) {
            printOptions(writer, deprecatedOptions, "Showing only deprecated properties.");
        } else {
            if (state.getParsedValue() != null) {
                if (isProperty(currentOptions, state.getParsedValue())) {
                    printDetailedHelp(writer, allOptions, state.getParsedValue());
                } else if (isPrintGroup(allGroups, state.getParsedValue())) {
                    printHelpFilteredByPrintGroup(writer, currentOptions, state.getParsedValue());
                } else {
                    printHelpFilteredBySearchTerm(writer, currentOptions, state.getParsedValue());
                }
            } else if (state.isDiagnostic() || state.isDiagnosticExtended()) {
                printStream.println(DIAGNOSTIC_HELP_TEXT);
            } else {
                printHelpFilteredByPrintGroup(writer, allOptions, defaultGroup.getName());
            }
        }

        printStandardFooter(writer, allGroups.stream().map(Group::getName).collect(Collectors.joining(", ")));

        writer.write(printStream);
    }

    private void printDetailedHelp(final HelpTextWriter writer, final List<Property> options, final String optionName) {
        final Property option = options.stream()
                                    .filter(it -> it.getKey().equals(optionName))
                                    .findFirst().orElse(null);

        if (option == null) {
            writer.println("Could not find option named: " + optionName);
        } else {
            printDetailedOption(writer, option);
        }
    }

    private void printHelpFilteredByPrintGroup(final HelpTextWriter writer, final List<Property> options, final String filterGroup) {
        final String notes = "Showing help only for: " + filterGroup;

        final List<Property> filteredOptions = options.stream()
                                                   .filter(detectOption -> optionMatchesFilterGroup(detectOption, filterGroup))
                                                   .collect(Collectors.toList());

        printOptions(writer, filteredOptions, notes);
    }

    private boolean optionMatchesFilterGroup(final Property property, final String filterGroup) {

        final boolean primaryMatches = property.getPropertyGroupInfo().getPrimaryGroup().getName().equalsIgnoreCase(filterGroup);
        final boolean additionalMatches = property.getPropertyGroupInfo().getAdditionalGroups().stream()
                                              .anyMatch(printGroup -> printGroup.getName().equalsIgnoreCase(filterGroup));
        return primaryMatches || additionalMatches;
    }

    private void printHelpFilteredBySearchTerm(final HelpTextWriter writer, final List<Property> options, final String searchTerm) {
        final String notes = "Showing help only for fields that contain: " + searchTerm;

        final List<Property> filteredOptions = options.stream()
                                                   .filter(it -> it.getKey().contains(searchTerm))
                                                   .collect(Collectors.toList());

        printOptions(writer, filteredOptions, notes);
    }

    private boolean isPrintGroup(final List<Group> allPrintGroups, final String filterGroup) {
        return allPrintGroups.stream().map(Group::getName).anyMatch(name -> name.equals(filterGroup));
    }

    private boolean isProperty(final List<Property> allOptions, final String filterTerm) {
        return allOptions.stream()
                   .map(Property::getKey)
                   .anyMatch(it -> it.equals(filterTerm));
    }

    public void printDetailedOption(final HelpTextWriter writer, final Property property) {
        writer.println("");
        writer.println("Detailed information for " + property.getKey());
        writer.println("");
        if (property.getPropertyDeprecationInfo() != null) {
            writer.println("Deprecated: " + property.getPropertyDeprecationInfo().getDeprecationText());
            writer.println("Deprecation description: " + property.getPropertyDeprecationInfo().getDescription());
            writer.println("");
        }
        writer.println("Property description: " + property.getPropertyHelpInfo().getShortText());

        writer.println("Property default value: " + property.describeDefault());
        if (property.listExampleValues().size() > 0) {
            writer.println("Property acceptable values: " + String.join(", ", property.listExampleValues()));
        }
        writer.println("");

        if (StringUtils.isNotBlank(property.getPropertyHelpInfo().getLongText())) {
            writer.println("Detailed help:");
            writer.println(property.getPropertyHelpInfo().getLongText());
            writer.println();
        }
    }

    public void printOption(final HelpTextWriter writer, final Property property) {
        String description = property.getPropertyHelpInfo().getShortText();
        if (property.getPropertyDeprecationInfo() != null) {
            description = property.getPropertyDeprecationInfo().getDeprecationText() + description;
        }
        if (property.listExampleValues().size() > 0) {
            description += " (" + String.join("|", property.listExampleValues()) + ")";
        }
        String propertyKey = property.getKey();
        String defaultValue = "";
        if (StringUtils.isNotBlank(property.describeDefault())) {
            defaultValue = property.describeDefault();
        }
        writer.printColumns("--" + propertyKey, defaultValue, description);
    }

    public void printOptions(final HelpTextWriter writer, final List<Property> options, final String notes) {
        writer.printColumns("Property Name", "Default", "Description");
        writer.printSeperator();

        final List<Property> sorted = options.stream()
                                          .sorted(SORT_BY_GROUP_THEN_KEY)
                                          .collect(Collectors.toList());

        if (notes != null) {
            writer.println(notes);
            writer.println();
        }

        String group = null;
        for (final Property detectOption : sorted) {
            final String currentGroup = detectOption.getPropertyGroupInfo().getPrimaryGroup().getName();
            if (group == null) {
                group = currentGroup;
                writer.println("[" + group + "]");
            } else if (!group.equals(currentGroup)) {
                group = currentGroup;
                writer.println();
                writer.println("[" + group + "]");
            }
            printOption(writer, detectOption);
        }
    }

    public void printStandardFooter(final HelpTextWriter writer, final String groupText) {
        writer.println();
        writer.println("To set a Detect property on the command line: ");
        writer.println("\t--<property name>=<value>");
        writer.println();
        writer.println("To see all properties, you may request verbose help log with '-hv'");
        writer.println("To see the hidden deprecated properties, you may request them with '-hd'");
        writer.println();
        writer.println("To get detailed help for a specific property, you may specify the property name with '-h [property]'");
        writer.println();
        writer.println("To print only a subset of options, you may specify one of the following printable groups with '-h [group]': ");
        writer.println("\t" + groupText);
        writer.println();
        writer.println("To search options, you may specify a search term with '-h [term]'");
        writer.println();
        writer.println("To run in interactive mode (which prompts you for the values needed for common use cases), run Detect with the argument '--interactive'");
        writer.println();
    }
}
