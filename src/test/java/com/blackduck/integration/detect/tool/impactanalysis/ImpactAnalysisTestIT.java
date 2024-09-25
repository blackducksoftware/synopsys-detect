package com.blackduck.integration.detect.tool.impactanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.blackduck.integration.blackduck.codelocation.CodeLocationCreationData;
import com.blackduck.integration.blackduck.codelocation.Result;
import com.blackduck.integration.blackduck.service.BlackDuckServicesFactory;
import com.blackduck.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.blackduck.integration.detect.tool.impactanalysis.GenerateImpactAnalysisOperation;
import com.blackduck.integration.detect.tool.impactanalysis.ImpactAnalysisNamingOperation;
import com.blackduck.integration.detect.tool.impactanalysis.ImpactAnalysisUploadOperation;
import com.blackduck.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.blackduck.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.blackduck.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.blackduck.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.blackduck.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.blackduck.integration.exception.IntegrationException;
import com.blackduck.integration.log.BufferedIntLogger;
import com.blackduck.integration.log.IntLogger;
import com.blackduck.integration.util.NameVersion;
import com.blackduck.integration.util.NoThreadExecutorService;

@Tag("integration")
public class ImpactAnalysisTestIT {
    private final CodeLocationNameGenerator codeLocationNameGenerator = CodeLocationNameGenerator.withPrefixSuffix("prefix", "suffix");
    private final CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(codeLocationNameGenerator);

    @TempDir
    File outputDirAsPath;
    private final IntLogger logger = new BufferedIntLogger();

    @Test
    public void testImpactAnalysisForDetect() throws IOException, IntegrationException {
        BlackDuckTestConnection blackDuckTestConnection = BlackDuckTestConnection.fromEnvironment();
        NameVersion projectNameVersion = new NameVersion("detect-junit", "impact-analysis");
        BlackDuckServicesFactory blackDuckServicesFactory = blackDuckTestConnection.getBlackDuckServicesFactory();

        File toScan = new File("./");
        Path outputDirectory = outputDirAsPath.toPath();

        ImpactAnalysisNamingOperation impactAnalysisNamingOperation = new ImpactAnalysisNamingOperation(codeLocationNameManager);
        String impactAnalysisCodeLocationName = impactAnalysisNamingOperation.createCodeLocationName(toScan, projectNameVersion);

        GenerateImpactAnalysisOperation generateImpactAnalysisOperation = new GenerateImpactAnalysisOperation();
        Path impactAnalysisFile = generateImpactAnalysisOperation.generateImpactAnalysis(toScan, impactAnalysisCodeLocationName, outputDirectory);

        ImpactAnalysisBatchRunner impactAnalysisBatchRunner = new ImpactAnalysisBatchRunner(
            logger,
            blackDuckServicesFactory.getBlackDuckApiClient(),
            blackDuckServicesFactory.getApiDiscovery(),
            new NoThreadExecutorService(),
            blackDuckServicesFactory.getGson()
        );
        ImpactAnalysisUploadService impactAnalysisUploadService = new ImpactAnalysisUploadService(
            impactAnalysisBatchRunner,
            blackDuckServicesFactory.createCodeLocationCreationService()
        );
        ImpactAnalysisUploadOperation impactAnalysisUploadOperation = new ImpactAnalysisUploadOperation(impactAnalysisUploadService);
        CodeLocationCreationData<ImpactAnalysisBatchOutput> creationData = impactAnalysisUploadOperation.uploadImpactAnalysis(
            impactAnalysisFile,
            projectNameVersion,
            impactAnalysisCodeLocationName
        );

        assertEquals(1, creationData.getOutput().getOutputs().size());
        assertEquals(Result.SUCCESS, creationData.getOutput().getOutputs().get(0).getResult());
    }

}
