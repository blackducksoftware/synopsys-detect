/*
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
package com.blackducksoftware.integration.hub.detect.help

import org.apache.commons.lang3.StringUtils
import org.springframework.stereotype.Component
import org.springframework.util.CollectionUtils

import groovy.transform.TypeChecked

@Component
@TypeChecked
class HelpPrinter {

    void printProfiles(PrintStream printStream, Set<String> profiles, List<String> selectedProfiles) {
        printStream.println("Available Profiles: " + profiles.join(', '));
        printStream.println("Selected Profiles: " + selectedProfiles.join(", "));
    }

    void printHelpMessage(PrintStream printStream, List<DetectOption> options, Set<String> profiles, List<String> selectedProfiles) {
        def helpMessagePieces = []
        helpMessagePieces.add('')

        printProfiles(printStream, profiles, selectedProfiles)

        def headerColumns = ['Property Name', 'Default', 'Description']

        String headerText = formatColumns(headerColumns, 50, 30, 95)
        helpMessagePieces.add(headerText)
        helpMessagePieces.add(StringUtils.repeat('_', 175))

        String group = null
        boolean atLeastOneInGroupPrinted = false

        options.each { detectValue ->
            String currentGroup = detectValue.getGroup()
            if (group == null) {
                group = currentGroup
                atLeastOneInGroupPrinted = false;
            } else if (!group.equals(currentGroup)) {
                if (atLeastOneInGroupPrinted){
                    helpMessagePieces.add('')
                }
                group = currentGroup
                atLeastOneInGroupPrinted = false;
            }

            String matchingProfileDefault = detectValue.getDefaultValue().matchingProfile(selectedProfiles);
            String actualDefaultValue = detectValue.getDefaultValue().defaultValue(selectedProfiles);

            String defaultValueHelp = detectValue.getDefaultValue().originalDefault;
            if (matchingProfileDefault != null){
                defaultValueHelp = matchingProfileDefault + ": " + actualDefaultValue;
            }

            def bodyColumns = ["--" + detectValue.getKey(), defaultValueHelp, detectValue.getDescription()]
            String bodyText = formatColumns(bodyColumns, 50, 30, 95)


            if (selectedProfiles.empty || CollectionUtils.containsAny(detectValue.getProfiles(), selectedProfiles)){
                helpMessagePieces.add(bodyText)
                atLeastOneInGroupPrinted=true;
            }
        }
        helpMessagePieces.add('')
        helpMessagePieces.add('Usage : ')
        helpMessagePieces.add('\t--<property name>=<value>')
        helpMessagePieces.add('')

        printStream.println(helpMessagePieces.join(System.getProperty("line.separator")))
    }

    private String defaultValue(DetectOption option) {
        String out = option.defaultValue.originalDefault;
        option.defaultValue.profileSpecificDefaults.each{
            out += " (" + it.key + ": " + it.value + ")"
        }
        return out;
    }

    private String formatColumns(List<String> columns, int... columnWidths) {
        StringBuilder createColumns = new StringBuilder()
        List<String> columnfirstRow = []
        List<String> columnRemainingRows = []
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).size() < columnWidths[i]) {
                columnfirstRow.add(columns.get(i))
                columnRemainingRows.add('')
            } else {
                String firstRow = columns.get(i).substring(0, columnWidths[i])
                int endOfWordIndex = firstRow.lastIndexOf(' ')
                if (endOfWordIndex == -1) {
                    endOfWordIndex = columnWidths[i] - 1
                    columnfirstRow.add(firstRow.substring(0, endOfWordIndex) + ' ')
                } else {
                    columnfirstRow.add(firstRow.substring(0, endOfWordIndex))
                }

                columnRemainingRows.add(columns.get(i).substring(endOfWordIndex).trim())
            }
        }

        for (int i = 0; i < columnfirstRow.size(); i++) {
            createColumns.append(StringUtils.rightPad(columnfirstRow.get(i), columnWidths[i], ' '))
        }

        if (!allColumnsEmpty(columnRemainingRows)) {
            createColumns.append(System.getProperty("line.separator") + formatColumns(columnRemainingRows, columnWidths))
        }
        createColumns.toString()
    }

    private boolean allColumnsEmpty(List<String> columns) {
        for (String column : columns) {
            if (column) {
                return false
            }
        }
        true
    }
}
