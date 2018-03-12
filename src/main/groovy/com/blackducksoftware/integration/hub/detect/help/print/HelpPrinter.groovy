/*
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
package com.blackducksoftware.integration.hub.detect.help.print

import org.apache.commons.lang3.StringUtils

import com.blackducksoftware.integration.hub.detect.help.DetectOption

import groovy.transform.TypeChecked

@TypeChecked
class HelpPrinter {
    private PrintStream printStream

    public HelpPrinter(PrintStream printStream) {
        this.printStream = printStream
    }

    void printHelpMessage(List<DetectOption> options) {
        def helpMessagePieces = []
        helpMessagePieces.add('')
        helpMessagePieces.add("To get further details on a specific property, please run -h 'specific.property.name'.")
        helpMessagePieces.add('')

        def headerColumns = [
            'Property Name',
            'Default',
            'Description'
        ]

        String headerText = formatColumns(headerColumns, 51, 30, 95)
        helpMessagePieces.add(headerText)
        helpMessagePieces.add(StringUtils.repeat('=', 175))

        String group = null

        options.each { detectValue ->
            String currentGroup = detectValue.getGroup()
            if (group == null) {
                group = currentGroup
            } else if (!group.equals(currentGroup)) {
                helpMessagePieces.add(StringUtils.repeat('=', 175))
                group = currentGroup
            } else {
                helpMessagePieces.add(StringUtils.repeat('_', 175))
            }

            def bodyColumns = [
                "--" + detectValue.getKey(),
                detectValue.getDefaultValue(),
                detectValue.getDescription()
            ]
            String bodyText = formatColumns(bodyColumns, 51, 30, 95)
            helpMessagePieces.add(bodyText)
        }
        helpMessagePieces.add('')
        helpMessagePieces.add('Usage : ')
        helpMessagePieces.add('\t--<property name>=<value>')
        helpMessagePieces.add('')

        printStream.println(helpMessagePieces.join(System.lineSeparator))
    }

    void printVerboseMessage() {
        def verboseMessagePieces = []

        verboseMessagePieces.add(' ')
        verboseMessagePieces.add('Basic list of properties:')
        verboseMessagePieces.add('(To see a more complete listing of all properties, please run the -v or --verbose command as well.)')

        printStream.println(verboseMessagePieces.join(System.lineSeparator))
    }

    void printHelpDetailedMessage(DetectOption detectOption) {
        def detailedMessage = []

        detailedMessage.add(' ')
        detailedMessage.add("Detailed information for ${detectOption.key}")
        detailedMessage.add(' ')
        detailedMessage.add("Property description: ${detectOption.description}")
        detailedMessage.add("Property default value: ${detectOption.defaultValue}")
        detailedMessage.add(' ')

        detailedMessage.add("Use cases: ${detectOption.useCases}")
        detailedMessage.add(' ')
        detailedMessage.add("Common issues: ${detectOption.issues}")
        detailedMessage.add(' ')

        printStream.println(detailedMessage.join(System.lineSeparator))
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
