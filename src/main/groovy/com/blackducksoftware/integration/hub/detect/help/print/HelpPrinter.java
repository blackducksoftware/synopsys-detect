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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.hub.detect.help.DetectOption;

@Component
public class HelpPrinter {

    public void printHelpMessage(final PrintStream printStream, final List<DetectOption> options, String filterGroup) {
        final List<String> helpMessagePieces = new ArrayList<>();
        helpMessagePieces.add("");
        
        Set<String> groups = new HashSet<String>();
        boolean filterByGroup = false; // must match at least one group
        if (filterGroup != null && filterGroup.trim().length() >= 0) {
            for (final DetectOption detectValue : options) {
                for (final String printGroup : detectValue.getPrintGroups()) {
                    if (printGroup.equalsIgnoreCase(filterGroup)) {
                        filterByGroup = true;
                    }
                    groups.add(printGroup);
                }
            }
        }
        

        final List<String> headerColumns = Arrays.asList("Property Name", "Default", "Description");
        final String headerText = formatColumns(headerColumns, 51, 30, 95);
        helpMessagePieces.add(headerText);
        helpMessagePieces.add(StringUtils.repeat('_', 175));

        String groupText = "";
        List<String> groupList = new ArrayList<String>(groups);
        java.util.Collections.sort(groupList);
        for (String group : groupList) {
            if (group.contains(" ")) continue;
            if (!groupText.equals("")) {
                groupText += ", ";
            }
            groupText += group;
        }
        
        if (filterByGroup) {
            helpMessagePieces.add("Showing help only for: " + filterGroup);
            helpMessagePieces.add("");
        }
        
        String group = null;

        for (final DetectOption detectValue : options) {
            final String currentGroup = detectValue.getGroup();
            
            if (filterByGroup) {
                boolean inAnyGroup = false;
                for (final String printGroup : detectValue.getPrintGroups()) {
                    inAnyGroup = inAnyGroup || printGroup.equalsIgnoreCase(filterGroup);
                }
                if (!inAnyGroup) continue;
            }
            
            if (group == null) {
                group = currentGroup;
            } else if (!group.equals(currentGroup)) {
                helpMessagePieces.add("");
                group = currentGroup;
            }

            final List<String> bodyColumns = Arrays.asList("--" + detectValue.getKey(), detectValue.getDefaultValue(), detectValue.getDescription());
            final String bodyText = formatColumns(bodyColumns, 51, 30, 95);
            helpMessagePieces.add(bodyText);
        }
        helpMessagePieces.add("");
        helpMessagePieces.add("Usage : ");
        helpMessagePieces.add("\t--<property name>=<value>");
        helpMessagePieces.add("");
        helpMessagePieces.add("To print only a subset of options, you may specify one of the following printable groups with '-h [group]' or '--help [group]': ");
        helpMessagePieces.add("\t" + groupText);
        helpMessagePieces.add("");

        printMessage(printStream, helpMessagePieces);
    }

    private void printMessage(final PrintStream printStream, final List<String> message) {
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
                    columnfirstRow.add(firstRow.substring(0, endOfWordIndex) + " ");
                } else {
                    columnfirstRow.add(firstRow.substring(0, endOfWordIndex));
                }

                columnRemainingRows.add(columns.get(i).substring(endOfWordIndex).trim());
            }
        }

        for (int i = 0; i < columnfirstRow.size(); i++) {
            createColumns.append(StringUtils.rightPad(columnfirstRow.get(i), columnWidths[i], " "));
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
