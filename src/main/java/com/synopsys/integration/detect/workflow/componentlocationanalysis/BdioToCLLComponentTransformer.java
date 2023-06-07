package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.detect.workflow.bdio.BdioResult;

/**
 * Transforms {@link BdioResult} to {@link CLLComponent}
 */
public class BdioToCLLComponentTransformer {
    public ComponentLocatorLibraryInput transformToComponentLocatorInput(BdioResult bdio) {
        return new ComponentLocatorLibraryInput(null, null, null);
    }
}
