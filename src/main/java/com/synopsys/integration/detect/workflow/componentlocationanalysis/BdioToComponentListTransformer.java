package com.synopsys.integration.detect.workflow.componentlocationanalysis;

import com.synopsys.integration.detect.workflow.bdio.BdioResult;

/**
 * Transforms {@link BdioResult} to list of {@link Component}s, which will then be used to assemble the input to
 * Component Locator.
 *
 * Will be fully implemented in a subsequent pull request to the Fix PR feature branch.
 */
public class BdioToComponentListTransformer {
    public ComponentLocatorInput transformBdioToComponentList(BdioResult bdio) {
        return new ComponentLocatorInput(null, null, null);
    }
}
