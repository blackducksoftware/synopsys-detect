package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.detect.workflow.bdio.BdioResult;

/**
 * Transforms {@link BdioResult} to {@link Component}, which will then be used to assemble the input to Component Locator.
 *
 * Will be fully implemented in a subsequent pull request to the Fix PR feature branch.
 */
public class BdioToComponentTransformer {
    public ComponentLocatorLibraryInput transformBdioToCLLInput(BdioResult bdio) {
        return new ComponentLocatorLibraryInput(null, null, null);
    }
}
