package com.synopsys.integration.detect.lifecycle.run.step;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.lifecycle.run.operation.input.BdioInput;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.UniversalToolsResultBuilder;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateDecision;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.util.NameVersion;

public class UniversalStepRunner {
    private OperationFactory operationFactory;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectToolFilter detectToolFilter; //TODO: This should be a decision

    public UniversalStepRunner(final OperationFactory operationFactory, DetectToolFilter detectToolFilter) { //TODO: Move to Step Helper
        this.operationFactory = operationFactory;
        this.detectToolFilter = detectToolFilter;
    }

    public UniversalToolsResult runUniversalTools() throws DetectUserFriendlyException, IntegrationException {
        UniversalToolsResultBuilder resultBuilder = new UniversalToolsResultBuilder();
        runDocker().ifPresent(resultBuilder::addDetectableToolResult);
        runBazel().ifPresent(resultBuilder::addDetectableToolResult);
        runDetectors().ifPresent(resultBuilder::addDetectorToolResult);
        return resultBuilder.build();
    }

    private Optional<DetectableToolResult> runDocker() throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.DOCKER)) {
            logger.info("Will include the Docker tool.");
            DetectableToolResult result = operationFactory.createDockerOperation().execute();
            logger.info("Docker actions finished.");
            return Optional.ofNullable(result);
        } else {
            logger.info("Docker tool will not be run.");
            return Optional.empty();
        }
    }

    private Optional<DetectableToolResult> runBazel() throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.BAZEL)) {
            logger.info("Will include the Bazel tool.");
            DetectableToolResult result = operationFactory.createBazelOperation().execute();
            logger.info("Bazel actions finished.");
            return Optional.ofNullable(result);
        } else {
            logger.info("Bazel tool will not be run.");
            return Optional.empty();
        }
    }

    private Optional<DetectorToolResult> runDetectors() throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        if (detectToolFilter.shouldInclude(DetectTool.DETECTOR)) {
            logger.info("Will include the detector tool.");
            DetectorToolResult result = operationFactory.createDetectorOperation().execute();
            logger.info("Detector actions finished.");
            return Optional.ofNullable(result);
        } else {
            logger.info("Detector tool will not be run.");
            return Optional.empty();
        }
    }

    public BdioResult generateBdio(UniversalToolsResult universalToolsResult, NameVersion projectNameVersion) throws DetectUserFriendlyException, IntegrationException {
        AggregateDecision aggregateDecision = operationFactory.createAggregateOptionsOperation().execute(universalToolsResult.didAnyFail());
        BdioInput bdioInput = new BdioInput(aggregateDecision, projectNameVersion, universalToolsResult.getDetectCodeLocations());
        return operationFactory.createBdioFileGenerationOperation().execute(bdioInput);
    }

    public NameVersion determineProjectInformation(UniversalToolsResult universalToolsResult) throws DetectUserFriendlyException, IntegrationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.debug("Completed code location tools.");

        logger.debug("Determining project info.");

        NameVersion projectNameVersion = operationFactory.createProjectDecisionOperation(universalToolsResult.getDetectToolProjectInfo());

        logger.info(String.format("Project name: %s", projectNameVersion.getName()));
        logger.info(String.format("Project version: %s", projectNameVersion.getVersion()));

        return projectNameVersion;
    }
}
