package com.blackduck.integration.detectable.detectables.npm;

import com.blackduck.integration.detectable.detectables.npm.lockfile.model.NpmRequires;
import org.slf4j.Logger;

@FunctionalInterface
public interface MissingNpmDependencyHandler {
    void handleMissingDependency(Logger logger, NpmRequires missingDependency);
}
