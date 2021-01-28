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

public class DockerOperation extends Operation<RunResult, RunResult> {
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
    protected boolean shouldExecute() {
        return detectToolFilter.shouldInclude(DetectTool.DOCKER);
    }

    @Override
    public String getOperationName() {
        return "Docker";
    }

    @Override
    protected OperationResult<RunResult> executeOperation(RunResult input) throws DetectUserFriendlyException, IntegrationException {
        DetectableTool detectableTool = new DetectableTool(detectDetectableFactory::createDockerDetectable,
            extractionEnvironmentProvider, codeLocationConverter, "DOCKER", DetectTool.DOCKER,
            eventSystem);

        DetectableToolResult detectableToolResult = detectableTool.execute(directoryManager.getSourceDirectory());

        input.addDetectableToolResult(detectableToolResult);
        OperationResult result;
        if (detectableToolResult.isFailure()) {
            result = OperationResult.fail(input);
        } else {
            result = OperationResult.success(input);
        }
        return result;
    }
}
