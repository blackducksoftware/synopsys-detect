package com.synopsys.integration.configuration.config.resolution;

import java.util.Optional;

import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.util.Assert;

public class SourcePropertyResolution extends PropertyResolution {
    @NotNull
    private PropertyResolutionInfo propertyResolutionInfo;

    public SourcePropertyResolution(final @NotNull PropertyResolutionInfo propertyResolutionInfo) {
        Assert.notNull(propertyResolutionInfo, "Cannot create a source property resolution without supplying property info, use NoPropertyResolution if this was intentional.");
        this.propertyResolutionInfo = propertyResolutionInfo;
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.of(propertyResolutionInfo);
    }
}
