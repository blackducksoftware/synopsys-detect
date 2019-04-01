/**
 * synopsys-detect
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector.inspectors;

import java.io.File;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.DetectInfo;
import com.synopsys.integration.detect.tool.detector.impl.DetectExecutableResolver;
import com.synopsys.integration.detect.workflow.file.AirGapManager;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunner;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;

public class AirgapNugetInspectorResolver extends AutomaticInstallerNugetInspectorResolver {
    private final Logger logger = LoggerFactory.getLogger(AirgapNugetInspectorResolver.class);
    private AirGapManager airGapManager;

    public AirgapNugetInspectorResolver(final DetectExecutableResolver executableResolver, final ExecutableRunner executableRunner, final DetectInfo detectInfo,
        final FileFinder fileFinder, final NugetInspectorOptions nugetInspectorOptions, final AirGapManager airGapManager) {
        super(executableResolver, executableRunner, detectInfo, fileFinder, nugetInspectorOptions);
        this.airGapManager = airGapManager;
    }

    @Override
    public File installExeInspector() {
        Optional<File> nugetAirGapPath = airGapManager.getNugetInspectorAirGapFile();
        return new File(nugetAirGapPath.get(), "nuget_dotnet");
    }

    @Override
    public File installDotnetInspector() {
        Optional<File> nugetAirGapPath = airGapManager.getNugetInspectorAirGapFile();
        return new File(nugetAirGapPath.get(), "nuget_classic");

    }
}
