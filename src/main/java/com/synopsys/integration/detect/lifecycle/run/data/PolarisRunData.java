/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.data;

import com.synopsys.integration.polaris.common.configuration.PolarisServerConfig;

public class PolarisRunData {
    private final PolarisServerConfig polarisServerConfig;

    public PolarisRunData(final PolarisServerConfig polarisServerConfig) {
        this.polarisServerConfig = polarisServerConfig;
    }

    public PolarisServerConfig getPolarisServerConfig() {
        return polarisServerConfig;
    }
}
