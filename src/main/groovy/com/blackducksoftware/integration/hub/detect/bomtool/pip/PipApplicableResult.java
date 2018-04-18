package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import java.io.File;

import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.model.BomToolType;

public class PipApplicableResult extends BomToolApplicableResult {
    private final File setupTools;
    private final File requirements;
    private final String pipExe;
    private final String pythonExe;

    public PipApplicableResult(final File directory, final File setupTools, final File requirements, final String pipExe, final String pythonExe) {
        super(directory, BomToolType.PIP);
        this.setupTools = setupTools;
        this.requirements = requirements;
        this.pipExe = pipExe;
        this.pythonExe = pythonExe;
    }

    public File getPackageXml() {
        return requirements;
    }

    public File getSetupTools() {
        return setupTools;
    }

    public String getPipExe() {
        return pipExe;
    }

    public String getPythonExe() {
        return pythonExe;
    }

}