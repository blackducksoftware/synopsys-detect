/**
 * detectable
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.synopsys.integration.detectable.detectables.nuget;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

public class NugetProjectDetectable extends Detectable {
    static final String[] SUPPORTED_PROJECT_PATTERNS = new String[] {
        // C#
        "*.csproj",
        // F#
        "*.fsproj",
        // VB
        "*.vbproj",
        // Azure Stream Analytics
        "*.asaproj",
        // Docker Compose
        "*.dcproj",
        // Shared Projects
        "*.shproj",
        // Cloud Computing
        "*.ccproj",
        // Fabric Application
        "*.sfproj",
        // Node.js
        "*.njsproj",
        // VC++
        "*.vcxproj",
        // VC++
        "*.vcproj",
        // .NET Core
        "*.xproj",
        // Python
        "*.pyproj",
        // Hive
        "*.hiveproj",
        // Pig
        "*.pigproj",
        // JavaScript
        "*.jsproj",
        // U-SQL
        "*.usqlproj",
        // Deployment
        "*.deployproj",
        // Common Project System Files
        "*.msbuildproj",
        // SQL
        "*.sqlproj",
        // SQL Project Files
        "*.dbproj",
        // RStudio
        "*.rproj"
    };

    private final FileFinder fileFinder;
    private final NugetInspectorOptions nugetInspectorOptions;
    private final NugetInspectorResolver nugetInspectorResolver;
    private final NugetInspectorExtractor nugetInspectorExtractor;

    private NugetInspector inspector;

    public NugetProjectDetectable(final DetectableEnvironment detectableEnvironment, final FileFinder fileFinder, final NugetInspectorOptions nugetInspectorOptions, final NugetInspectorResolver nugetInspectorResolver,
        final NugetInspectorExtractor nugetInspectorExtractor) {
        super(detectableEnvironment, "nuget", "nuget");
        this.fileFinder = fileFinder;
        this.nugetInspectorOptions = nugetInspectorOptions;
        this.nugetInspectorResolver = nugetInspectorResolver;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
    }

    @Override
    public DetectableResult applicable() {
        for (final String filepattern : SUPPORTED_PROJECT_PATTERNS) {
            if (fileFinder.findFile(environment.getDirectory(), filepattern) != null) {
                return new PassedDetectableResult();
            }
        }
        return new FilesNotFoundDetectableResult(SUPPORTED_PROJECT_PATTERNS);
    }

    @Override
    public DetectableResult extractable() throws DetectableException {
        inspector = nugetInspectorResolver.resolveNugetInspector();

        if (inspector == null) {
            return new InspectorNotFoundDetectableResult("nuget");
        }

        return new PassedDetectableResult();
    }

    @Override
    public Extraction extract(final ExtractionEnvironment extractionEnvironment) {
        return nugetInspectorExtractor.extract(environment.getDirectory(), extractionEnvironment.getOutputDirectory(), inspector, nugetInspectorOptions);
    }

}