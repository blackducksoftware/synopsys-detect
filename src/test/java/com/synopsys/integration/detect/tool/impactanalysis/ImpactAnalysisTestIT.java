package com.synopsys.integration.detect.tool.impactanalysis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.synopsys.integration.blackduck.codelocation.CodeLocationCreationData;
import com.synopsys.integration.blackduck.codelocation.Result;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.battery.docker.integration.BlackDuckTestConnection;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchOutput;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisBatchRunner;
import com.synopsys.integration.detect.tool.impactanalysis.service.ImpactAnalysisUploadService;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.BufferedIntLogger;
import com.synopsys.integration.log.IntLogger;
import com.synopsys.integration.util.NameVersion;
import com.synopsys.integration.util.NoThreadExecutorService;

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
        NameVersion projectNameVersion = new NameVersion("synopsys-detect-junit", "impact-analysis");
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
