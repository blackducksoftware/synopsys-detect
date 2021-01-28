package com.synopsys.integration.detect.lifecycle.run.operation;

import com.synopsys.integration.detect.lifecycle.run.RunContext;
import com.synopsys.integration.detect.tool.impactanalysis.ImpactAnalysisOptions;

public class OperationFactory {
    private final RunContext runContext;

    public OperationFactory(RunContext runContext) {
        this.runContext = runContext;
    }

    public final PolarisOperation createPolarisOperation() {
        return new PolarisOperation(runContext.getProductRunData(), runContext.getDetectConfiguration(), runContext.getDirectoryManager(), runContext.createRunOptions().getDetectToolFilter(), runContext.getEventSystem());
    }

    public final DockerOperation createDockerOperation() {
        return new DockerOperation(runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(), runContext.createRunOptions().getDetectToolFilter(), runContext.getExtractionEnvironmentProvider(),
            runContext.getCodeLocationConverter());
    }

    public final BazelOperation createBazelOperation() {
        return new BazelOperation(runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(), runContext.createRunOptions().getDetectToolFilter(), runContext.getExtractionEnvironmentProvider(),
            runContext.getCodeLocationConverter());
    }

    public final DetectorOperation createDetectorOperation() {
        return new DetectorOperation(runContext.getDetectConfiguration(), runContext.getDetectConfigurationFactory(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectDetectableFactory(),
            runContext.createRunOptions().getDetectToolFilter(),
            runContext.getExtractionEnvironmentProvider(), runContext.getCodeLocationConverter());
    }

    public final FullScanOperation createFullScanOperation(boolean hasPriorOperationsSucceeded) {
        ImpactAnalysisOptions impactAnalysisOptions = runContext.getDetectConfigurationFactory().createImpactAnalysisOptions();
        return new FullScanOperation(runContext.getDetectContext(), runContext.getDetectInfo(), runContext.getProductRunData(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectConfigurationFactory(),
            runContext.createRunOptions().getDetectToolFilter(),
            runContext.getCodeLocationNameManager(), runContext.getBdioCodeLocationCreator(), runContext.createRunOptions(), hasPriorOperationsSucceeded, impactAnalysisOptions);

    }

    public final RapidScanOperation createRapidScanOperation(boolean hasPriorOperationsSucceeded) {
        return new RapidScanOperation(runContext.getDetectContext(), runContext.getDetectInfo(), runContext.getProductRunData(), runContext.getDirectoryManager(), runContext.getEventSystem(), runContext.getDetectConfigurationFactory(),
            runContext.createRunOptions().getDetectToolFilter(),
            runContext.getCodeLocationNameManager(), runContext.getBdioCodeLocationCreator(), runContext.createRunOptions(), hasPriorOperationsSucceeded, runContext.getGson());
    }
}
