package com.blackducksoftware.integration.hub.detect.bomtool.cpan;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CpanApplicableResult extends BomToolApplicableResult {
    private final String cpanExePath;
    private final String cpanmExePath;
    private final File makefile;

    public CpanApplicableResult(final File directory, final File makefile, final String cpanExePath, final String cpanmExePath) {
        super(directory, BomToolType.CPAN);
        this.cpanExePath = cpanExePath;
        this.cpanmExePath = cpanmExePath;
        this.makefile = makefile;
    }

    public String getCpanExePath() {
        return cpanExePath;
    }

    public String getCpanmExePath() {
        return cpanmExePath;
    }

    public File getMakefile() {
        return makefile;
    }

}
