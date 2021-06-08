package com.synopsys.integration.detect.tool.impactanalysis;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import com.synopsys.integration.blackduck.codelocation.Result;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.service.model.ProjectSyncModel;
import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.integration.BlackDuckIntegrationTest;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.NoThreadExecutorService;
import org.junit.jupiter.api.io.TempDir;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("integration")
public class ImpactAnalysisTest extends BlackDuckIntegrationTest {
    private final CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(null);
    private final CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(codeLocationNameGenerator);

    @TempDir
    File outputDirAsPath;

    @Test
    public void testImpactAnalysisForDetect() throws IOException, IntegrationException {
        File toScan = new File("./");
        Path outputDirectory = outputDirAsPath.toPath();
        NameVersion projectNameVersion = new NameVersion("synopsys-detect-junit", "impact-analysis");
        ProjectVersionWrapper projectAndVersion = projectService.syncProjectAndVersion(ProjectSyncModel.createWithDefaults(projectNameVersion));

        ImpactAnalysisOptions impactAnalysisOptions = new ImpactAnalysisOptions("prefix", "suffix", outputDirectory);
        ImpactAnalysisNamingOperation impactAnalysisNamingOperation = new ImpactAnalysisNamingOperation(codeLocationNameManager);
        String impactAnalysisCodeLocationName = impactAnalysisNamingOperation.createCodeLocationName(toScan, projectNameVersion, impactAnalysisOptions);

        GenerateImpactAnalysisOperation generateImpactAnalysisOperation = new GenerateImpactAnalysisOperation();
        Path impactAnalysisFile = generateImpactAnalysisOperation.generateImpactAnalysis(toScan, impactAnalysisCodeLocationName, outputDirectory);

        ImpactAnalysisBatchRunner impactAnalysisBatchRunner = new ImpactAnalysisBatchRunner(logger, blackDuckApiClient, apiDiscovery, new NoThreadExecutorService(), gson);
        ImpactAnalysisUploadService impactAnalysisUploadService = new ImpactAnalysisUploadService(impactAnalysisBatchRunner, codeLocationCreationService);
        ImpactAnalysisUploadOperation impactAnalysisUploadOperation = new ImpactAnalysisUploadOperation(impactAnalysisUploadService);
        CodeLocationCreationData<ImpactAnalysisBatchOutput> creationData = impactAnalysisUploadOperation.uploadImpactAnalysis(impactAnalysisFile, projectNameVersion, impactAnalysisCodeLocationName);

        assertEquals(1, creationData.getOutput().getOutputs().size());
        assertEquals(Result.SUCCESS, creationData.getOutput().getOutputs().get(0).getResult());

        blackDuckApiClient.delete(projectAndVersion.getProjectView());
    }

}
