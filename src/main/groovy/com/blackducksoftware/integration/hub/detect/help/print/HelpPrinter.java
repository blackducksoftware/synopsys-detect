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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

public class HelpPrinter {
    PrintStream printStream;

    public HelpPrinter(final PrintStream printStream) {
        this.printStream = printStream;
    }

    public void printHelpMessage(final List<DetectOption> options) {
        final List<String> helpMessagePieces = new ArrayList<>();
        helpMessagePieces.add("");
        helpMessagePieces.add("To get further details on a specific property, please run -h -p specific.property.name.");
        helpMessagePieces.add("To get a list of all property groups, please run -h -g. With a group name, you can print all properties of a spceific group using -h -g groupName");
        helpMessagePieces.add("");

        final List<String> headerColumns = Arrays.asList("Property Name", "Default", "Description");
        final String headerText = formatColumns(headerColumns, 51, 30, 95);
        helpMessagePieces.add(headerText);
        helpMessagePieces.add(StringUtils.repeat('=', 175));

        String group = null;

        for (final DetectOption detectValue : options) {
            final String currentGroup = detectValue.getGroup();
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                helpMessagePieces.add(StringUtils.repeat('=', 175));
                group = currentGroup;
            } else {
                helpMessagePieces.add(StringUtils.repeat('_', 175));
            }

            final List<String> bodyColumns = Arrays.asList("--" + detectValue.getKey(), detectValue.getDefaultValue(), detectValue.getDescription());
            final String bodyText = formatColumns(bodyColumns, 51, 30, 95);
            helpMessagePieces.add(bodyText);
        }
        helpMessagePieces.add("");
        helpMessagePieces.add("Usage : ");
        helpMessagePieces.add("\t--<property name>=<value>");
        helpMessagePieces.add("");

        printMessage(helpMessagePieces);
    }

    public void printVerboseMessage() {
        final List<String> verboseMessagePieces = new ArrayList<>();

        verboseMessagePieces.add("");
        verboseMessagePieces.add("Basic list of properties:");
        verboseMessagePieces.add("(To see a more complete listing of all properties, please run the -h -v command.)");

        printMessage(verboseMessagePieces);
    }

    public void printHelpDetailedMessage(final DetectOption detectOption) {
        final List<String> detailedMessage = new ArrayList<>();

        detailedMessage.add("");
        detailedMessage.add("Detailed information for " + detectOption.getKey());
        detailedMessage.add("");
        detailedMessage.add("Property description: " + detectOption.getDescription());
        detailedMessage.add("Property default value: " + detectOption.getDefaultValue());
        detailedMessage.add("");

        detailedMessage.add("Use cases: " + detectOption.getUseCases());
        detailedMessage.add("");
        detailedMessage.add("Common issues: " + detectOption.getIssues());
        detailedMessage.add("");

        printMessage(detailedMessage);
    }

    public void printHelpGroupsMessage(final List<String> detectGroups) {
        final List<String> groupsMessage = new ArrayList<>();

        groupsMessage.add("");
        groupsMessage.add("To get a list of all properties related to a group, please run -h -g groupName");
        groupsMessage.add("");

        groupsMessage.add("Group Listing:");
        detectGroups.forEach(group -> groupsMessage.add(group));
        groupsMessage.add("");

        printMessage(groupsMessage);
    }

    public void printUnknownCommandMessage() {

    }

    private void printMessage(final List<String> message) {
        printStream.println(String.join(System.lineSeparator(), message));
    }

    private String formatColumns(final List<String> columns, final int... columnWidths) {
        final StringBuilder createColumns = new StringBuilder();
        final List<String> columnfirstRow = new ArrayList<>();
        final List<String> columnRemainingRows = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).length() < columnWidths[i]) {
                columnfirstRow.add(columns.get(i));
                columnRemainingRows.add("");
            } else {
                final String firstRow = columns.get(i).substring(0, columnWidths[i]);
                int endOfWordIndex = firstRow.lastIndexOf(' ');
                if (endOfWordIndex == -1) {
                    endOfWordIndex = columnWidths[i] - 1;
                    columnfirstRow.add(firstRow.substring(0, endOfWordIndex) + ' ');
                } else {
                    columnfirstRow.add(firstRow.substring(0, endOfWordIndex));
                }

                columnRemainingRows.add(columns.get(i).substring(endOfWordIndex).trim());
            }
        }

        for (int i = 0; i < columnfirstRow.size(); i++) {
            createColumns.append(StringUtils.rightPad(columnfirstRow.get(i), columnWidths[i], ' '));
        }

        if (!allColumnsEmpty(columnRemainingRows)) {
            createColumns.append(System.lineSeparator() + formatColumns(columnRemainingRows, columnWidths));
        }
        return createColumns.toString();
    }

    private boolean allColumnsEmpty(final List<String> columns) {
        for (final String column : columns) {
            if (!column.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
