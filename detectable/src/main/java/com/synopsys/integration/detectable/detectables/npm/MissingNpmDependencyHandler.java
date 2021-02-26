/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.npm;

import org.slf4j.Logger;

import com.synopsys.integration.detectable.detectables.npm.lockfile.model.NpmRequires;

@FunctionalInterface
public interface MissingNpmDependencyHandler {
    void handleMissingDependency(Logger logger, NpmRequires missingDependency);
}
