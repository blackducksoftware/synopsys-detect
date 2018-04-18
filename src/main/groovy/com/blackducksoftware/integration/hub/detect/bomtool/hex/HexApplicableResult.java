package com.blackducksoftware.integration.hub.detect.bomtool.hex;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class HexApplicableResult extends BomToolApplicableResult {
    private final File rebarConfig;
    private final String rebarExe;

    public HexApplicableResult(final File searchedDirectory, final File rebarConfig, final String rebarExe) {
        super(searchedDirectory, BomToolType.HEX);
        this.rebarConfig = rebarConfig;
        this.rebarExe = rebarExe;
    }

    public File getRebarConfig() {
        return rebarConfig;
    }

    public String getRebarExe() {
        return rebarExe;
    }

}