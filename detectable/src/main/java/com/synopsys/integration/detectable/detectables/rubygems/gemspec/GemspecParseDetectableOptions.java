/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.rubygems.gemspec;

public class GemspecParseDetectableOptions {
    final boolean includeRuntimeDependencies;
    final boolean includeDevelopmentDependencies;

    public GemspecParseDetectableOptions(final boolean includeRuntimeDependencies, final boolean includeDevelopmentDependencies) {
        this.includeRuntimeDependencies = includeRuntimeDependencies;
        this.includeDevelopmentDependencies = includeDevelopmentDependencies;
    }

    public boolean shouldIncludeRuntimeDependencies() {
        return includeRuntimeDependencies;
    }

    public boolean shouldIncludeDevelopmentDependencies() {
        return includeDevelopmentDependencies;
    }
}
