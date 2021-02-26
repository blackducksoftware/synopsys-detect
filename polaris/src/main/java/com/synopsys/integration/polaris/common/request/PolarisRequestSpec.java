/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.request;

import org.apache.commons.lang3.StringUtils;

public class PolarisRequestSpec {
    private final String spec;

    public static final PolarisRequestSpec of(final String spec) {
        return new PolarisRequestSpec(spec);
    }

    public PolarisRequestSpec(final String spec) {
        this.spec = spec;
    }

    public String getSpec() {
        return spec;
    }

    public String getType() {
        final String[] pieces = StringUtils.splitByWholeSeparator(spec, "/");
        return pieces[pieces.length - 1];
    }

    @Override
    public String toString() {
        return getSpec();
    }

}
