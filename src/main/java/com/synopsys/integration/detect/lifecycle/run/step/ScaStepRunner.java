package com.synopsys.integration.detect.lifecycle.run.step;

import java.io.File;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.github.jsonldjava.shaded.com.google.common.collect.ImmutableList;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;
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
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.status.OperationType;
import com.synopsys.integration.util.NameVersion;

public class ScaStepRunner {
    
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
                "Distributed SCA",
                OperationType.INTERNAL,
                () -> performDistributedSca(universalToolsResult, scaOptions));
        }
    }
    
    private void performDistributedSca(UniversalToolsResult universalToolsResult, ScaOptions scaOptions) throws OperationException {
        Set<FastScaEvidence> evidences = convert(universalToolsResult);
        UUID scanId = UUID.randomUUID();
        String version = operationRunner.getDetectVersion();
        File targetFile = new File(scaOptions.getOutputFile(), "fastSCA".concat(scanId.toString()).concat(".json"));
        FastScaMetadata meta = new FastScaMetadata(scanId, "DETECT", version, OffsetDateTime.now(), nameVersion.getName(), nameVersion.getVersion());
        FastScaReportApi fastScaReportApi = new FastScaReportApi(scaOptions.getKbScheme(), scaOptions.getKbHost(), scaOptions.getKbPort(), scaOptions.getInvalidLicenseKey(), scaOptions.getUser());
        fastScaReportApi.create(evidences, meta, targetFile);
    }
    
    private Set<FastScaEvidence> convert(UniversalToolsResult universalToolsResult) {
        List<DetectCodeLocation> detectCodeLocations = universalToolsResult.getDetectCodeLocations();
        SetMultimap<FastScaDependency, FastScaDependencyTree> evidences = HashMultimap.create();
        
        // Each Detect code location is a project or subproject within the top-level code target.
        for (DetectCodeLocation detectCodeLocation : detectCodeLocations) {
            DependencyGraph dependencyGraph = detectCodeLocation.getDependencyGraph();
            Set<Dependency> directDependencies = dependencyGraph.getDirectDependencies();

            int depth = 1;
            List<String> ongoingDependencyTree = new ArrayList<>();
            String projectExternalId = detectCodeLocation.getExternalId().createExternalId();
            ongoingDependencyTree.add(projectExternalId);

            for (Dependency directDependency : directDependencies) {
                String externalNamespace = directDependency.getExternalId().getForge().getName();
                String externalId = directDependency.getExternalId().createExternalId();
                FastScaDependency fastScaDependency = new FastScaDependency(externalNamespace, externalId);
                ongoingDependencyTree.add(externalId);

                List<String> dependencyTree = ImmutableList.copyOf(ongoingDependencyTree);
                FastScaDependencyType dependencyType = (1 == depth) ? FastScaDependencyType.DIRECT : FastScaDependencyType.TRANSITIVE;
                FastScaDependencyTree fastScaDependencyTree = new FastScaDependencyTree(dependencyTree, dependencyType);
                evidences.put(fastScaDependency, fastScaDependencyTree);

                depth++;
            }
        }
        
        Set<FastScaEvidence> results = new HashSet<>();
        Set<FastScaDependency> fastScaDependencies = evidences.keySet();
        for (FastScaDependency fastScaDependency : fastScaDependencies) {
            Set<FastScaDependencyTree> fastScaDependencyTrees = evidences.get(fastScaDependency);
            FastScaEvidence fastScaEvidence = new FastScaEvidence(fastScaDependency, fastScaDependencyTrees);
            results.add(fastScaEvidence);
        }
        
        return results;
    }
}