package com.synopsys.integration.detect.poc;

import java.util.List;

public class FileLocation {
    String filePath;
    List<LineLocation> lineLocations;

    public FileLocation(String filePath, List<LineLocation> lineLocations) {
        this.filePath = filePath;
        this.lineLocations = lineLocations;
    }
}
