package com.synopsys.integration.configuration.config.resolution;

import java.util.Optional;

public class NoPropertyResolution extends PropertyResolution {
    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.empty();
    }
}