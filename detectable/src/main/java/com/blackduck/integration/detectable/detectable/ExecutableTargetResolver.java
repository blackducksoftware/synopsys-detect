package com.blackduck.integration.detectable.detectable;

import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface ExecutableTargetResolver {
    ExecutableTarget resolve() throws DetectableException;
}
