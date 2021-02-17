/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
