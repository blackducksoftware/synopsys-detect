/**
 * synopsys-detect
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
package com.synopsys.integration.detect.lifecycle.run.operation;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.RunResult;
import com.synopsys.integration.detect.tool.DetectableTool;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;

public class DockerOperation extends Operation<RunResult, Void> {
    private DirectoryManager directoryManager;
    private EventSystem eventSystem;
    private DetectDetectableFactory detectDetectableFactory;
    private DetectToolFilter detectToolFilter;
    private ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private CodeLocationConverter codeLocationConverter;

    public DockerOperation(DirectoryManager directoryManager, EventSystem eventSystem, DetectDetectableFactory detectDetectableFactory, DetectToolFilter detectToolFilter,
        ExtractionEnvironmentProvider extractionEnvironmentProvider, CodeLocationConverter codeLocationConverter) {
        this.directoryManager = directoryManager;
        this.eventSystem = eventSystem;
        this.detectDetectableFactory = detectDetectableFactory;
        this.detectToolFilter = detectToolFilter;
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.codeLocationConverter = codeLocationConverter;
    }

    @Override
    public boolean shouldExecute() {
        return detectToolFilter.shouldInclude(DetectTool.DOCKER);
    }

    @Override
    public String getOperationName() {
        return "Docker";
    }

    @Override
    public OperationResult<Void> executeOperation(RunResult input) throws DetectUserFriendlyException, IntegrationException {
        DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createDockerDetectable,
            extractionEnvironmentProvider, codeLocationConverter, "DOCKER", DetectTool.DOCKER,
            eventSystem);

        DetectableToolResult detectableToolResult = detectableTool.execute(directoryManager.getSourceDirectory());

        input.addDetectableToolResult(detectableToolResult);
        OperationResult<Void> result;
        if (detectableToolResult.isFailure()) {
            result = OperationResult.fail();
        } else {
            result = OperationResult.success();
        }
        return result;
    }
}
