/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.inspector.nuget;

import java.io.File;
import java.io.IOException;

import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public interface NugetInspector {
    ExecutableOutput execute(final File workingDirectory, final File targetFile, final File outputDirectory, final NugetInspectorOptions nugetInspectorOptions) throws ExecutableRunnerException, IOException;
}