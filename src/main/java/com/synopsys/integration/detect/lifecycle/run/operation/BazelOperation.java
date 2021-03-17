/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.operation;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.DetectableTool;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;

public class BazelOperation {
    private final DirectoryManager directoryManager;
    private final StatusEventPublisher statusEventPublisher;
    private final ExitCodePublisher exitCodePublisher;
    private final DetectDetectableFactory detectDetectableFactory;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final CodeLocationConverter codeLocationConverter;
    private final OperationSystem operationSystem;

    public BazelOperation(DirectoryManager directoryManager, StatusEventPublisher statusEventPublisher, ExitCodePublisher exitCodePublisher, DetectDetectableFactory detectDetectableFactory, DetectToolFilter detectToolFilter,
        ExtractionEnvironmentProvider extractionEnvironmentProvider, CodeLocationConverter codeLocationConverter, OperationSystem operationSystem) {
        this.directoryManager = directoryManager;
        this.statusEventPublisher = statusEventPublisher;
        this.exitCodePublisher = exitCodePublisher;
        this.detectDetectableFactory = detectDetectableFactory;
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.codeLocationConverter = codeLocationConverter;
        this.operationSystem = operationSystem;
    }

    public DetectableToolResult execute() {
        DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createBazelDetectable,
            extractionEnvironmentProvider, codeLocationConverter, "BAZEL", DetectTool.BAZEL,
            statusEventPublisher, exitCodePublisher, operationSystem);
        DetectableToolResult detectableToolResult = detectableTool.execute(directoryManager.getSourceDirectory());
        return detectableToolResult;
    }
}
