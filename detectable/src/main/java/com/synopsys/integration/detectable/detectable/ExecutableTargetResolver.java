package com.synopsys.integration.detectable.detectable;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface ExecutableTargetResolver {
    ExecutableTarget resolve() throws DetectableException;
}
