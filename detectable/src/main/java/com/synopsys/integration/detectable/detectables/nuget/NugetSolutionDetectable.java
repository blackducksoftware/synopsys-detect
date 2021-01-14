/**
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
package com.synopsys.integration.detectable.detectables.nuget;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspector;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorOptions;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.InspectorNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PassedDetectableResult;

@DetectableInfo(language = "C#", forge = "NuGet.org", requirementsMarkdown = "File: a solution file with a .sln extension.")
public class NugetSolutionDetectable extends Detectable {
    private static final List<String> SUPPORTED_SOLUTION_PATTERNS = Collections.singletonList("*.sln");

    private final FileFinder fileFinder;
    private final NugetInspectorResolver nugetInspectorResolver;
    private final NugetInspectorExtractor nugetInspectorExtractor;

    private final NugetInspectorOptions nugetInspectorOptions;
    private NugetInspector inspector;
    private List<File> solutionFiles = new ArrayList<>();

    public NugetSolutionDetectable(final DetectableEnvironment environment, final FileFinder fileFinder, final NugetInspectorResolver nugetInspectorManager, final NugetInspectorExtractor nugetInspectorExtractor,
        final NugetInspectorOptions nugetInspectorOptions) {
        super(environment);
        this.fileFinder = fileFinder;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
        this.nugetInspectorResolver = nugetInspectorManager;
        this.nugetInspectorOptions = nugetInspectorOptions;
    }

    @Override
    public DetectableResult applicable() {
        solutionFiles = fileFinder.findFiles(environment.getDirectory(), SUPPORTED_SOLUTION_PATTERNS);

        if (solutionFiles != null && solutionFiles.size() > 0) {
            return new PassedDetectableResult();
        } else {
            return new FilesNotFoundDetectableResult(SUPPORTED_SOLUTION_PATTERNS);
        }
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
        final File outputDirectory = extractionEnvironment.getOutputDirectory();
        return nugetInspectorExtractor.extract(solutionFiles, outputDirectory, inspector, nugetInspectorOptions);
    }

}
