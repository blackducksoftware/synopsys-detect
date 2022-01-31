package com.synopsys.integration.detectable.detectable.executable.resolver;

import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface GoResolver {
    @Nullable
    ExecutableTarget resolveGo() throws DetectableException;
}
