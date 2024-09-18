package com.blackduck.integration.detectable.detectable;

import com.blackduck.integration.detectable.detectable.exception.DetectableException;
import com.blackduck.integration.detectable.ExecutableTarget;

public interface ExecutableTargetResolver {
    ExecutableTarget resolve() throws DetectableException;
}
