package com.synopsys.integration.detect.tool.impactanalysis.service;

import java.nio.file.Path;

import com.synopsys.integration.util.NameVersion;

public class ImpactAnalysis {
    private final Path impactAnalysisPath;
    private final NameVersion projectAndVersion;
    private final String codeLocationName;

    public ImpactAnalysis(Path impactAnalysisPath, String projectName, String projectVersion, String codeLocationName) {
        this.impactAnalysisPath = impactAnalysisPath;
        this.projectAndVersion = new NameVersion(projectName, projectVersion);
        this.codeLocationName = codeLocationName;
    }

    public Path getImpactAnalysisPath() {
        return impactAnalysisPath;
    }

    public NameVersion getProjectAndVersion() {
        return projectAndVersion;
    }

    public String getCodeLocationName() {
        return codeLocationName;
    }

}
