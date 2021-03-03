/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation.input;

import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.util.NameVersion;

public class RapidScanInput {
    private final NameVersion projectNameVersion;
    private final BdioResult bdioResult;

    public RapidScanInput(NameVersion projectNameVersion, BdioResult bdioResult) {
        this.projectNameVersion = projectNameVersion;
        this.bdioResult = bdioResult;
    }

    public NameVersion getProjectNameVersion() {
        return projectNameVersion;
    }

    public BdioResult getBdioResult() {
        return bdioResult;
    }
}
