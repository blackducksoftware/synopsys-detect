package com.blackducksoftware.integration.hub.detect.bomtool.pear;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class PearApplicableResult extends BomToolApplicableResult {
    private final File packageXml;
    private final String pearExe;

    public PearApplicableResult(final File directory, final File packageXml, final String pearExe) {
        super(directory, BomToolType.PEAR);
        this.packageXml = packageXml;
        this.pearExe = pearExe;
    }

    public File getPackageXml() {
        return packageXml;
    }

    public String getPearExe() {
        return pearExe;
    }

}