/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.docker;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface DockerInspectorResolver {
    DockerInspectorInfo resolveDockerInspector() throws DetectableException;
}
