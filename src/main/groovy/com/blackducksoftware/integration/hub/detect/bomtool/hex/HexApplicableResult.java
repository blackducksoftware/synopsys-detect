package com.blackducksoftware.integration.hub.detect.bomtool.hex;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class HexApplicableResult extends BomToolApplicableResult {
    private final File rebarConfig;
    private final String rebarExe;

    public HexApplicableResult(final File directory, final File rebarConfig, final String rebarExe) {
        super(directory, BomToolType.HEX);
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