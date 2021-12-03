package com.synopsys.integration.detect.configuration.help.print;

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
        List<String> headerColumns = Arrays.asList(columns);
        String headerText = formatColumns(headerColumns, 69, 30, 77);
        pieces.add(headerText);
    }

    public void printSeparator() {
        println(StringUtils.repeat('_', 175));
    }

    public void write(PrintStream printStream) {
        printStream.println(String.join(System.lineSeparator(), pieces));
    }

    private String formatColumns(List<String> columns, int... columnWidths) {
        StringBuilder createColumns = new StringBuilder();
        List<String> columnfirstRow = new ArrayList<>();
        List<String> columnRemainingRows = new ArrayList<>();
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).length() < columnWidths[i]) {
                columnfirstRow.add(columns.get(i));
                columnRemainingRows.add("");
            } else {
                String firstRow = columns.get(i).substring(0, columnWidths[i]);
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

    private boolean allColumnsEmpty(List<String> columns) {
        for (String column : columns) {
            if (!column.isEmpty()) {
                return false;
            }
        }
        return true;
    }

}
