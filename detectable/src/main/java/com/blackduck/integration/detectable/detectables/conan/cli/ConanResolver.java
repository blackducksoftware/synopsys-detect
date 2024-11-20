package com.blackduck.integration.detectable.detectables.conan.cli;

import com.blackduck.integration.detectable.DetectableEnvironment;
import com.blackduck.integration.detectable.ExecutableTarget;
import com.blackduck.integration.detectable.detectable.exception.DetectableException;

public interface ConanResolver {
    ExecutableTarget resolveConan(DetectableEnvironment environment) throws DetectableException;
}
