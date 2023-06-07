package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.detect.workflow.bdio.BdioResult;

/**
 * Transforms {@link BdioResult} to {@link ComponentLocatorLibInput}
 */
public class BdioToComponentLocatorInputTransformer {
    public ComponentLocatorLibInput transformToComponentLocatorInput(BdioResult bdio) {
        return new ComponentLocatorLibInput(null, null, null);
    }
    // future enhancement: determine direct deps in BDIO and only add those to ComponentLocatorInput
}
