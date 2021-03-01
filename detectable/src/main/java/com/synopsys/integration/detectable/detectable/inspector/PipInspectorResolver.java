/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.inspector;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface PipInspectorResolver {
    File resolvePipInspector() throws DetectableException;
}
