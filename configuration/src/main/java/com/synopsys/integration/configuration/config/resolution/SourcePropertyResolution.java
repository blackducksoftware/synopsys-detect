package com.synopsys.integration.configuration.config.resolution;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.springframework.util.Assert;

public class SourcePropertyResolution extends PropertyResolution {
    @NotNull
    private final PropertyResolutionInfo propertyResolutionInfo;

    public SourcePropertyResolution(@NotNull PropertyResolutionInfo propertyResolutionInfo) {
        Assert.notNull(propertyResolutionInfo, "Cannot create a source property resolution without supplying property info, use NoPropertyResolution if this was intentional.");
        this.propertyResolutionInfo = propertyResolutionInfo;
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.of(propertyResolutionInfo);
    }
}
