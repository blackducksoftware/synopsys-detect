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
import java.util.ArrayList;
import java.util.List;

import com.synopsys.integration.detectable.ExecutableTarget;
import com.synopsys.integration.detectable.ExecutableUtils;
import com.synopsys.integration.detectable.detectable.executable.DetectableExecutableRunner;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public class DotNetCoreNugetInspector implements NugetInspector {
    private final ExecutableTarget dotnetExe;
    private final String inspectorDll;
    private final DetectableExecutableRunner executableRunner;

    public DotNetCoreNugetInspector(ExecutableTarget dotnetExe, String inspectorDll, DetectableExecutableRunner executableRunner) {
        this.dotnetExe = dotnetExe;
        this.inspectorDll = inspectorDll;
        this.executableRunner = executableRunner;
    }

    @Override
    public ExecutableOutput execute(File workingDirectory, File targetFile, File outputDirectory, NugetInspectorOptions nugetInspectorOptions) throws ExecutableRunnerException, IOException {
        List<String> dotnetArguments = new ArrayList<>();
        dotnetArguments.add(inspectorDll);
        dotnetArguments.addAll(NugetInspectorArguments.fromInspectorOptions(nugetInspectorOptions, targetFile, outputDirectory));

        return executableRunner.execute(ExecutableUtils.createFromTarget(workingDirectory, dotnetExe, dotnetArguments));
    }
}
