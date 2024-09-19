package com.blackduck.integration.detect.tool.impactanalysis;

import java.io.File;

import com.blackduck.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackduck.integration.util.NameVersion;

public class ImpactAnalysisNamingOperation {
    private final CodeLocationNameManager codeLocationNameManager;

    public ImpactAnalysisNamingOperation(CodeLocationNameManager codeLocationNameManager) {
        this.codeLocationNameManager = codeLocationNameManager;
    }

    public String createCodeLocationName(File toScan, NameVersion projectNameAndVersion) {
        String projectName = projectNameAndVersion.getName();
        String projectVersionName = projectNameAndVersion.getVersion();
        return codeLocationNameManager.createImpactAnalysisCodeLocationName(toScan, projectName, projectVersionName);
    }

}
