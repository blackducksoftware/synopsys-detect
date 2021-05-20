package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.util.NameVersion;

public class AggregateCodeLocation {
    private final File aggregateFile;
    private final String codeLocationName;
    private final NameVersion projectNameVersion;
    private final ExternalId projectExternalId;
    private final DependencyGraph aggregateDependencyGraph;

    public AggregateCodeLocation(final File aggregateFile, final String codeLocationName, final NameVersion projectNameVersion, final ExternalId projectExternalId,
        final DependencyGraph aggregateDependencyGraph) {
        this.aggregateFile = aggregateFile;
        this.codeLocationName = codeLocationName;
        this.projectNameVersion = projectNameVersion;
        this.projectExternalId = projectExternalId;
        this.aggregateDependencyGraph = aggregateDependencyGraph;
    }

    public File getAggregateFile() {
        return aggregateFile;
    }

    public String getCodeLocationName() {
        return codeLocationName;
    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public ExternalId getProjectExternalId() {
        return projectExternalId;
    }

    public DependencyGraph getAggregateDependencyGraph() {
        return aggregateDependencyGraph;
    }
}
