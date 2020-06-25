package com.synopsys.integration.detect.tool.impactanalysis;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.method.analyzer.core.MethodUseAnalyzer;

public class BlackDuckImpactAnalysisTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DirectoryManager directoryManager;
    private final CodeLocationNameManager codeLocationNameManager;

    public BlackDuckImpactAnalysisTool(DirectoryManager directoryManager, CodeLocationNameManager codeLocationNameManager) {
        this.directoryManager = directoryManager;
        this.codeLocationNameManager = codeLocationNameManager;
    }

    public void run(NameVersion projectNameVersion) {
        MethodUseAnalyzer analyzer = new MethodUseAnalyzer();
        try {
            String impactAnalysisCodeLocationName = codeLocationNameManager.createImpactAnalysisCodeLocationName(directoryManager.getSourceDirectory(), projectNameVersion.getName(), projectNameVersion.getVersion(), null, null);
            Path outputReportFile = analyzer.analyze(directoryManager.getSourceDirectory().toPath(), directoryManager.getBinaryOutputDirectory().toPath(), impactAnalysisCodeLocationName);
            logger.info(String.format("Vulnerability Impact Analysis generated report at %s", outputReportFile));

            // TODO: Upload file to Black Duck
        } catch (IOException exception) {
            logger.error("Vulnerability Impact Analysis failed.", exception);
        }
    }
}
