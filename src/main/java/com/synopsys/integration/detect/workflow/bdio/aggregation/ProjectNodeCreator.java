package com.synopsys.integration.detect.workflow.bdio.aggregation;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;

@FunctionalInterface
public interface ProjectNodeCreator {
    Dependency create(String name, String version, ExternalId externalId);

}
