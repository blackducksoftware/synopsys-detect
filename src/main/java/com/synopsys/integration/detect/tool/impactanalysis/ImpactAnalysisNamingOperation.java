package com.synopsys.integration.detect.tool.impactanalysis;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.util.NameVersion;

public class ImpactAnalysisNamingOperation {
    private final CodeLocationNameManager codeLocationNameManager;

    public ImpactAnalysisNamingOperation(CodeLocationNameManager codeLocationNameManager) {
        this.codeLocationNameManager = codeLocationNameManager;
    }

    public String createCodeLocationName(File toScan, NameVersion projectNameAndVersion, ImpactAnalysisOptions impactAnalysisOptions) throws IOException {
        String projectName = projectNameAndVersion.getName();
        String projectVersionName = projectNameAndVersion.getVersion();
        String codeLocationPrefix = impactAnalysisOptions.getCodeLocationPrefix();
        String codeLocationSuffix = impactAnalysisOptions.getCodeLocationSuffix();
        return codeLocationNameManager.createImpactAnalysisCodeLocationName(toScan, projectName, projectVersionName, codeLocationPrefix, codeLocationSuffix);
    }

}
