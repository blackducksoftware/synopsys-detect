package com.blackduck.integration.detectable.detectable.executable.resolver;

import org.jetbrains.annotations.Nullable;

import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface GoResolver {
    @Nullable
    ExecutableTarget resolveGo() throws DetectableException;
}
