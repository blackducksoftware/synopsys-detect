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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class HelpPrinter {

    @Autowired
    ValueDescriptionAnnotationFinder valueDescriptionAnnotationFinder

    void printHelpMessage(PrintStream printStream) {
        def helpMessagePieces = []
        helpMessagePieces.add('')

        def headerColumns = [
            'Property Name',
            'Default',
            'Type',
            'Description'
        ]

        String headerText = formatColumns(headerColumns, 50, 30, 20, 75)
        helpMessagePieces.add(headerText)
        helpMessagePieces.add(StringUtils.repeat('_', 175))
        String group = null
        valueDescriptionAnnotationFinder.getDetectValues().each { detectValue ->
            String currentGroup = detectValue.getGroup()
            if (group == null) {
                group = currentGroup
            } else if (!group.equals(currentGroup)) {
                helpMessagePieces.add(StringUtils.repeat(' ', 175))
                group = currentGroup
            }
            def bodyColumns = [
                detectValue.getKey(),
                detectValue.getDefaultValue(),
                detectValue.getValueType().getSimpleName(),
                detectValue.getDescription()
            ]
            String bodyText = formatColumns(bodyColumns, 50, 30, 20, 75)
            helpMessagePieces.add(bodyText)
        }
        helpMessagePieces.add('')
        helpMessagePieces.add('Usage : ')
        helpMessagePieces.add('\t--<property name>=<value>')
        helpMessagePieces.add('')

        printStream.println(StringUtils.join(helpMessagePieces, System.getProperty("line.separator")))
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
