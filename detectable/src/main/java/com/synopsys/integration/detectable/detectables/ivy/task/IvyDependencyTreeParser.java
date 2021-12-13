package com.synopsys.integration.detectable.detectables.ivy.task;

import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class IvyDependencyTreeParser {
    private final ExternalIdFactory externalIdFactory;

    public IvyDependencyTreeParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    //TODO- implment!
    public DependencyGraph parse(List<String> dependencytreeOutput) {

    }
}
