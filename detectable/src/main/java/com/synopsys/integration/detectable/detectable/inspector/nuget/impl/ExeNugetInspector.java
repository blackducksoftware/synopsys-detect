/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.inspector.nuget.impl;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class ExeNugetInspector implements NugetInspector {
    private final String inspectorExe;
    private final DetectableExecutableRunner executableRunner;

    public ExeNugetInspector(final DetectableExecutableRunner executableRunner, final String inspectorExe) {
        this.executableRunner = executableRunner;
        this.inspectorExe = inspectorExe;
    }

    @Override
    public ExecutableOutput execute(final File workingDirectory, final File sourcePath, final File outputDirectory, final NugetInspectorOptions nugetInspectorOptions) throws ExecutableRunnerException, IOException {
        final List<String> arguments = NugetInspectorArguments.fromInspectorOptions(nugetInspectorOptions, sourcePath, outputDirectory);
        return executableRunner.execute(workingDirectory, inspectorExe, arguments);
    }
}