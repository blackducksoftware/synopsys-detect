package com.synopsys.integration.detectable.detectable.executable.resolver;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface SwiftResolver {
    ExecutableTarget resolveSwift() throws DetectableException;
}
