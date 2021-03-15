/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.util.NameVersion;

public class ImpactAnalysisInput {
    private final NameVersion projectNameVersion;
    private final ProjectVersionWrapper projectVersionWrapper;

    public ImpactAnalysisInput(NameVersion projectNameVersion, ProjectVersionWrapper projectVersionWrapper) {
        this.projectNameVersion = projectNameVersion;
        this.projectVersionWrapper = projectVersionWrapper;
    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public ProjectVersionWrapper getProjectVersionWrapper() {
        return projectVersionWrapper;
    }
}
