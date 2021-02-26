/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import com.synopsys.integration.blackduck.service.model.ProjectVersionWrapper;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.blackduck.codelocation.CodeLocationResults;
import com.synopsys.integration.util.NameVersion;

public class FullScanPostProcessingInput {
    private final NameVersion projectNameVersion;
    private final BdioResult bdioResult;
    private final CodeLocationResults codeLocationResults;
    private final ProjectVersionWrapper projectVersionWrapper;

    public FullScanPostProcessingInput(NameVersion projectNameVersion, BdioResult bdioResult, CodeLocationResults codeLocationResults, ProjectVersionWrapper projectVersionWrapper) {
        this.projectNameVersion = projectNameVersion;
        this.bdioResult = bdioResult;
        this.codeLocationResults = codeLocationResults;
        this.projectVersionWrapper = projectVersionWrapper;

    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public BdioResult getBdioResult() {
        return bdioResult;
    }

    public CodeLocationResults getCodeLocationResults() {
        return codeLocationResults;
    }

    public ProjectVersionWrapper getProjectVersionWrapper() {
        return projectVersionWrapper;
    }

}
