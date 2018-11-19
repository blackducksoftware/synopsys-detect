package com.blackducksoftware.integration.hub.detect.util.filter;

import com.synopsys.integration.util.ExcludedIncludedFilter;

public class DetectNameFilter extends ExcludedIncludedFilter implements DetectFilter {

    /**
     * Provide a comma-separated list of names to exclude and/or a comma-separated list of names to include. Exclusion rules always win.
     * @param toExclude
     * @param toInclude
     */
    public DetectNameFilter(final String toExclude, final String toInclude) {
        super(toExclude, toInclude);
    }
}
