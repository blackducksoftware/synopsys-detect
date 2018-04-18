package com.blackducksoftware.integration.hub.detect.bomtool.conda;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class CondaApplicableResult extends BomToolApplicableResult {
    private final String condaExePath;
    private final File environmentYml;

    public CondaApplicableResult(final File directory, final File environmentYml, final String condaExePath) {
        super(directory, BomToolType.CONDA);
        this.condaExePath = condaExePath;
        this.environmentYml = environmentYml;
    }

    public String getCondaExePath() {
        return condaExePath;
    }

    public File getEnvironmentYml() {
        return environmentYml;
    }

}
