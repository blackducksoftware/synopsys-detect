package com.blackducksoftware.integration.hub.detect.bomtool.go;

import java.io.File;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class GoDepApplicableResult extends BomToolApplicableResult {
    private final File goPkg;
    private final List<File> goFiles;
    private final String goExe;

    public GoDepApplicableResult(final File searchedDirectory, final File goPkg, final List<File> goFiles, final String goExe) {
        super(searchedDirectory, BomToolType.GO_DEP);
        this.goPkg = goPkg;
        this.goFiles = goFiles;
        this.goExe = goExe;
    }

    public String getGoExe() {
        return goExe;
    }

    public File getGoPkg() {
        return goPkg;
    }

    public List<File> getGoFiles() {
        return goFiles;
    }

}
