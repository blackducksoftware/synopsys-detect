package com.blackducksoftware.integration.hub.detect.workflow.bdio;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.BdioCodeLocation;

public class BdioResult {
    private final List<BdioCodeLocation> bdioCodeLocations;
    private final List<File> bdioFiles;

    public BdioResult(final List<BdioCodeLocation> bdioCodeLocations, List<File> bdioFiles) {
        this.bdioCodeLocations = bdioCodeLocations;
        this.bdioFiles = bdioFiles;
    }

    public List<BdioCodeLocation> getBdioCodeLocations() {
        return bdioCodeLocations;
    }

    public List<File> getBdioFiles() {
        return bdioFiles;
    }
}
