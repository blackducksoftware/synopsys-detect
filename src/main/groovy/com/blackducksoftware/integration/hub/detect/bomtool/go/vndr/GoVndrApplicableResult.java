package com.blackducksoftware.integration.hub.detect.bomtool.go.vndr;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class GoVndrApplicableResult extends BomToolApplicableResult {
    private final File vendorConf;

    public GoVndrApplicableResult(final File directory, final File vendorConf) {
        super(directory, BomToolType.GO_VNDR);
        this.vendorConf = vendorConf;
    }

    public File getVenderConf() {
        return vendorConf;
    }


}
