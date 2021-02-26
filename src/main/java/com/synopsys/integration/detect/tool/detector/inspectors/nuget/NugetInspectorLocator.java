/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool.detector.inspectors.nuget;

import java.io.File;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public interface NugetInspectorLocator {
    File locateDotnet3Inspector() throws DetectableException;

    File locateDotnetInspector() throws DetectableException;

    File locateExeInspector() throws DetectableException;
}
