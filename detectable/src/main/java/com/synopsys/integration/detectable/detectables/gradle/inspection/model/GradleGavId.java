/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.gradle.inspection.model;

import com.synopsys.integration.bdio.model.dependencyid.DependencyId;

public interface GradleGavId {
    DependencyId toDependencyId();
}
