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
package com.synopsys.integration.detect.detector.nuget;

import java.io.File;

import com.synopsys.integration.detect.detector.Detector;
import com.synopsys.integration.detect.detector.DetectorEnvironment;
import com.synopsys.integration.detect.detector.DetectorException;
import com.synopsys.integration.detect.detector.DetectorType;
import com.synopsys.integration.detect.detector.ExtractionId;
import com.synopsys.integration.detect.detector.nuget.inspector.NugetInspector;
import com.synopsys.integration.detect.workflow.extraction.Extraction;
import com.synopsys.integration.detect.workflow.file.DetectFileFinder;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.search.result.DetectorResult;
import com.synopsys.integration.detect.workflow.search.result.FilesNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.InspectorNotFoundDetectorResult;
import com.synopsys.integration.detect.workflow.search.result.PassedDetectorResult;

public class NugetSolutionDetector extends Detector {
    static final String[] SUPPORTED_SOLUTION_PATTERNS = new String[] { "*.sln" };

    private final DetectFileFinder fileFinder;
    private final NugetInspectorManager nugetInspectorManager;
    private final NugetInspectorExtractor nugetInspectorExtractor;

    private NugetInspector inspector;
    private DirectoryManager directoryManager;

    public NugetSolutionDetector(final DetectorEnvironment environment, final DetectFileFinder fileFinder, final NugetInspectorManager nugetInspectorManager, final NugetInspectorExtractor nugetInspectorExtractor,
        final DirectoryManager directoryManager) {
        super(environment, "Solution", DetectorType.NUGET);
        this.fileFinder = fileFinder;
        this.nugetInspectorExtractor = nugetInspectorExtractor;
        this.nugetInspectorManager = nugetInspectorManager;
        this.directoryManager = directoryManager;
    }

    @Override
    public DetectorResult applicable() {
        for (final String filepattern : SUPPORTED_SOLUTION_PATTERNS) {
            if (fileFinder.findFile(environment.getDirectory(), filepattern) != null) {
                return new PassedDetectorResult();
            }
        }
        return new FilesNotFoundDetectorResult(SUPPORTED_SOLUTION_PATTERNS);
    }

    @Override
    public DetectorResult extractable() throws DetectorException {
        inspector = nugetInspectorManager.findNugetInspector();

        if (inspector == null) {
            return new InspectorNotFoundDetectorResult("nuget");
        }

        return new PassedDetectorResult();
    }

    @Override
    public Extraction extract(final ExtractionId extractionId) {
        final File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return nugetInspectorExtractor.extract(environment.getDirectory(), outputDirectory, inspector, extractionId);
    }

}
