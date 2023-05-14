package com.synopsys.integration.detect.lifecycle.run.step;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.enumeration.ScaStrategy;
import com.synopsys.integration.detect.fastsca.model.FastScaDependency;
import com.synopsys.integration.detect.fastsca.model.FastScaDependencyTree;
import com.synopsys.integration.detect.fastsca.model.FastScaDependencyType;
import com.synopsys.integration.detect.fastsca.model.FastScaEvidence;
import com.synopsys.integration.detect.fastsca.model.FastScaMetadata;
import com.synopsys.integration.detect.fastsca.options.ScaOptions;
import com.synopsys.integration.detect.fastsca.report.FastScaReportApi;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.status.OperationType;
import com.synopsys.integration.util.NameVersion;
import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScaStepRunner {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OperationRunner operationRunner;
    private final StepHelper stepHelper;
    private final UniversalToolsResult universalToolsResult;
    private final NameVersion nameVersion;
    
    public ScaStepRunner(OperationRunner operationRunner, StepHelper stepHelper, UniversalToolsResult universalToolsResult, NameVersion nameVersion) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
        this.universalToolsResult = universalToolsResult;
        this.nameVersion = nameVersion;
    }
    
    public void run() throws OperationException {
        ScaOptions scaOptions = operationRunner.getScaOptions();
        if (scaOptions.getStrategy().equals(ScaStrategy.DISTRIBUTED)) {
            stepHelper.runAsGroup(
                "Distributed fastSCA",
                OperationType.INTERNAL,
                () -> performDistributedSca(universalToolsResult, scaOptions));
        }
    }
    
    private void performDistributedSca(UniversalToolsResult universalToolsResult, ScaOptions scaOptions) throws OperationException {
        logger.info("Call fastSCA");
        String kbHost = scaOptions.getKbHost();
        String licenseKey = scaOptions.getLicenseKey();
        Set<FastScaEvidence> evidences = convert(universalToolsResult);
        UUID scanId = UUID.randomUUID();
        File targetFile = new File(scaOptions.getOutputFile(), "fastSCA".concat(scanId.toString()).concat(".json"));
        FastScaMetadata meta = new FastScaMetadata(scanId, "DETECT", "8.9.0", OffsetDateTime.now(), 
                nameVersion.getName(), nameVersion.getVersion());
        FastScaReportApi fastScaReportApi = new FastScaReportApi("https", "kbtest.blackducksoftware.com", 443, "gopanna_hub", "gopanna");
        fastScaReportApi.create(evidences, meta, targetFile);
    }
    
    private Set<FastScaEvidence> convert(UniversalToolsResult universalToolsResult) {
        Set<FastScaEvidence> evidences = new HashSet<>();
        List<DetectCodeLocation> detectCodeLocations = universalToolsResult.getDetectCodeLocations();
        for (DetectCodeLocation detectCodeLocation : detectCodeLocations) {
            FastScaDependency fastScaDependency = new FastScaDependency("GRADLE", 
                    detectCodeLocation.getExternalId().createExternalId());
            List<String> dependencyTree = new ArrayList<>();
            detectCodeLocation.getDependencyGraph().getDirectDependencies().forEach(action -> {
                dependencyTree.add(action.getExternalId().createExternalId());
            });
            FastScaDependencyTree fastScaDependencyTree = new FastScaDependencyTree(dependencyTree, FastScaDependencyType.DIRECT);
            Set<FastScaDependencyTree> fastScaDependencyTrees = new HashSet<>();
            fastScaDependencyTrees.add(fastScaDependencyTree);
            FastScaEvidence fastScaEvidence = new FastScaEvidence(fastScaDependency, fastScaDependencyTrees);
            evidences.add(fastScaEvidence);
        }
        return evidences;
    }
}