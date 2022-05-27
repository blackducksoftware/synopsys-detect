package com.synopsys.integration.detect.workflow.bdio;

import java.io.File;

import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.util.NameVersion;

public class AggregateCodeLocation {
    private final File aggregateFile;
    private final String codeLocationName;
    private final NameVersion projectNameVersion;
    private final ProjectDependencyGraph aggregateDependencyGraph;

    public AggregateCodeLocation(
        File aggregateFile,
        String codeLocationName,
        NameVersion projectNameVersion,
        ProjectDependencyGraph aggregateDependencyGraph
    ) {
        this.aggregateFile = aggregateFile;
        this.codeLocationName = codeLocationName;
        this.projectNameVersion = projectNameVersion;
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

    public ProjectDependencyGraph getAggregateDependencyGraph() {
        return aggregateDependencyGraph;
    }
}
