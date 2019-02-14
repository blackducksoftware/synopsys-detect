package com.synopsys.integration.detectable.detectable.executable.resolver;

import java.io.File;

import com.synopsys.integration.detectable.DetectableEnvironment;

public interface NpmResolver {
    File resolveNpm(final DetectableEnvironment environment);
}
