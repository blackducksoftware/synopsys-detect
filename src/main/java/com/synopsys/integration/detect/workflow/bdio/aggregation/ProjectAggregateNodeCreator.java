/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.bdio.aggregation;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.blackduck.bdio.model.dependency.ProjectDependency;

// This creates aggregate (top level / codelocation / subproject) dependency nodes for BD 2021.10+
public class ProjectAggregateNodeCreator implements AggregateNodeCreator {
    @Override
    public Dependency create(String name, String version, ExternalId externalId) {
        return new ProjectDependency(name, version, externalId);
    }
}
