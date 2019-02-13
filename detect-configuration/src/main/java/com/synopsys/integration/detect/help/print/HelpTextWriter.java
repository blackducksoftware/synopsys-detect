/**
 * detect-configuration
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.synopsys.integration.detect.help.print;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class HelpTextWriter {

    private final List<String> pieces = new ArrayList<>();
    
    public void println() {
        println("");
    }
    
    public void println(String line) {
        pieces.add(line);
    }
    
    public void printColumns(String... columns) {
        final List<String> headerColumns = Arrays.asList(columns);
        final String headerText = formatColumns(headerColumns, 51, 30, 95);
        pieces.add(headerText);
    }
    
    public void printSeperator() {
        println(StringUtils.repeat('_', 175));
    }
    
    public void write(final PrintStream printStream) {
        printStream.println(String.join(System.lineSeparator(), pieces));
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
