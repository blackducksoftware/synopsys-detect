package com.synopsys.integration.detectable.detectable.executable.resolver;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface GradleResolver {
    File resolveGradle(DetectableEnvironment environment) throws DetectableException;
}
