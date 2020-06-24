package com.synopsys.integration.detect.tool.impactanalysis;

import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.method.analyzer.core.MethodUseAnalyzer;

public class BlackDuckImpactAnalysisTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final DirectoryManager directoryManager;

    public BlackDuckImpactAnalysisTool(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public void run(String projectName) {
        MethodUseAnalyzer analyzer = new MethodUseAnalyzer();
        try {
            Path outputReportFile = analyzer.analyze(directoryManager.getSourceDirectory().toPath(), directoryManager.getBinaryOutputDirectory().toPath(), projectName);
            logger.info(String.format("Vulnerability Impact Analysis generated report at %s", outputReportFile));

            // TODO: Upload file to Black Duck
        } catch (IOException exception) {
            logger.error("Vulnerability Impact Analysis failed.", exception);
        }
    }
}
