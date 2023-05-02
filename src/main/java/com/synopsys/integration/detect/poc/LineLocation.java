package com.synopsys.integration.detect.poc;

import java.util.List;

public class LineLocation {
    int lineNumber;
    List<ColumnLocation> columnLocations;

    public LineLocation(int lineNumber, List<ColumnLocation> columnLocations) {
        this.lineNumber = lineNumber;
        this.columnLocations = columnLocations;
    }
}
