/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.config.resolution;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class PropertyResolutionInfo {
    @NotNull
    private String source;
    @NotNull
    private String origin;
    @NotNull
    private String raw;

    public PropertyResolutionInfo(@NotNull final String source, @NotNull final String origin, @NotNull final String raw) {
        Assert.notNull(source, "Source cannot be null.");
        Assert.notNull(origin, "Origin cannot be null.");
        Assert.notNull(raw, "Raw cannot be null.");
        this.source = source;
        this.origin = origin;
        this.raw = raw;
    }

    @NotNull
    public String getSource() {
        return source;
    }

    @NotNull
    public String getOrigin() {
        return origin;
    }

    @NotNull
    public String getRaw() {
        return raw;
    }
}
