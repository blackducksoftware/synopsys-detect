/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.config.resolution;

import java.util.Optional;

public abstract class PropertyResolution {
    public abstract Optional<PropertyResolutionInfo> getResolutionInfo();
}
