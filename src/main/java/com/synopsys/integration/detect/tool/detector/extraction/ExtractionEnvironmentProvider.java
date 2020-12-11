/**
 * synopsys-detect
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detect.tool.detector.extraction;

import java.io.File;

import com.synopsys.integration.detect.tool.detector.DetectExtractionEnvironment;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectorEvaluation;

public class ExtractionEnvironmentProvider {
    private final DirectoryManager directoryManager;
    private int count = 0;

    public ExtractionEnvironmentProvider(DirectoryManager directoryManager) {
        this.directoryManager = directoryManager;
    }

    public ExtractionEnvironment createExtractionEnvironment(DetectorEvaluation detectorEvaluation) {
        ExtractionId extractionId = new ExtractionId(detectorEvaluation.getDetectorType(), count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }

    public ExtractionEnvironment createExtractionEnvironment(String name) {
        ExtractionId extractionId = new ExtractionId(name, count);
        count = count + 1;

        File outputDirectory = directoryManager.getExtractionOutputDirectory(extractionId);
        return new DetectExtractionEnvironment(outputDirectory, extractionId);
    }
}
