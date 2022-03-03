package com.synopsys.integration.detect.tool.impactanalysis;

import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysis;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class ImpactAnalysisUploadOperation {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ImpactAnalysisUploadService impactAnalysisUploadService;

    public ImpactAnalysisUploadOperation(ImpactAnalysisUploadService impactAnalysisUploadService) {
        this.impactAnalysisUploadService = impactAnalysisUploadService;
    }

    public CodeLocationCreationData<ImpactAnalysisBatchOutput> uploadImpactAnalysis(Path impactAnalysisPath, NameVersion projectNameVersion, String codeLocationName)
        throws IntegrationException {
        ImpactAnalysis impactAnalysis = new ImpactAnalysis(impactAnalysisPath, projectNameVersion.getName(), projectNameVersion.getVersion(), codeLocationName);
        CodeLocationCreationData<ImpactAnalysisBatchOutput> codeLocationCreationData = impactAnalysisUploadService.uploadImpactAnalysis(impactAnalysis);
        ImpactAnalysisBatchOutput impactAnalysisBatchOutput = codeLocationCreationData.getOutput();
        impactAnalysisBatchOutput.throwExceptionForError(logger);
        return codeLocationCreationData;
    }

}
