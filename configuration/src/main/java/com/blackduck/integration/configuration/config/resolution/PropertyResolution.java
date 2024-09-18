package com.blackduck.integration.configuration.config.resolution;

import java.util.Optional;

public abstract class PropertyResolution {
    public abstract Optional<PropertyResolutionInfo> getResolutionInfo();
}
