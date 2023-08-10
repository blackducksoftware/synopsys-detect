package com.synopsys.integration.detect.lifecycle.run.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.bdio2.Bdio;
import com.synopsys.integration.bdio.graph.ProjectDependencyGraph;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.lifecycle.OperationException;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationRunner;
import com.synopsys.integration.detect.lifecycle.run.step.utility.StepHelper;
import com.synopsys.integration.detect.tool.DetectableTool;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.UniversalToolsResultBuilder;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.workflow.bdio.AggregateCodeLocation;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;
import com.synopsys.integration.detect.workflow.report.util.ReportConstants;
import com.synopsys.integration.util.NameVersion;

public class UniversalStepRunner {
    private final OperationRunner operationRunner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final StepHelper stepHelper;

    public UniversalStepRunner(OperationRunner operationRunner, StepHelper stepHelper) {
        this.operationRunner = operationRunner;
        this.stepHelper = stepHelper;
    }

    public UniversalToolsResult runUniversalTools() throws OperationException {
        UniversalToolsResultBuilder resultBuilder = new UniversalToolsResultBuilder();

        stepHelper.runToolIfIncluded(DetectTool.DOCKER, "Docker", this::runDocker)
            .ifPresent(resultBuilder::addDetectableToolResult);

        stepHelper.runToolIfIncluded(DetectTool.BAZEL, "Bazel", this::runBazel)
            .ifPresent(resultBuilder::addDetectableToolResult);

        stepHelper.runToolIfIncluded(DetectTool.DETECTOR, "Detectors", this::runDetectors)
            .ifPresent(resultBuilder::addDetectorToolResult);

        return resultBuilder.build();
    }

    private DetectableToolResult runDocker() throws OperationException {
        Optional<DetectableTool> potentialTool = operationRunner.checkForDocker();
        if (potentialTool.isPresent()) {
            return operationRunner.executeDocker(potentialTool.get());
        } else {
            return DetectableToolResult.skip();
        }
    }

    private DetectableToolResult runBazel() throws OperationException {
        Optional<DetectableTool> potentialTool = operationRunner.checkForBazel();
        if (potentialTool.isPresent()) {
            return operationRunner.executeBazel(potentialTool.get());
        } else {
            return DetectableToolResult.skip();
        }
    }

    private DetectorToolResult runDetectors() throws OperationException {
        DetectorToolResult result = operationRunner.executeDetectors();
        if (result.anyDetectorsFailed()) {
            operationRunner.publishDetectorFailure();
        }
        return result;
    }

    public BdioResult generateBdio(UniversalToolsResult universalToolsResult, NameVersion projectNameVersion) throws OperationException {
        ProjectDependencyGraph aggregateDependencyGraph = operationRunner.aggregateSubProject(projectNameVersion, universalToolsResult.getDetectCodeLocations());

        AggregateCodeLocation aggregateCodeLocation = operationRunner.createAggregateCodeLocation(
            aggregateDependencyGraph,
            projectNameVersion,
            universalToolsResult.getDetectToolGitInfo()
        );
        operationRunner.createAggregateBdio2File(aggregateCodeLocation, Bdio.ScanType.PACKAGE_MANAGER);

        List<UploadTarget> uploadTargets = new ArrayList<>();
        Map<DetectCodeLocation, String> codeLocationNamesResult = new HashMap<>();
        universalToolsResult.getDetectCodeLocations().forEach(cl -> codeLocationNamesResult.put(
            cl,
            aggregateCodeLocation.getCodeLocationName()
        )); //TODO: This doesn't seem right, it should just be the aggregate CL name right?

        uploadTargets.add(UploadTarget.createDefault(projectNameVersion, aggregateCodeLocation.getCodeLocationName(), aggregateCodeLocation.getAggregateFile()));

        return new BdioResult(uploadTargets, new DetectCodeLocationNamesResult(codeLocationNamesResult), universalToolsResult.getApplicableDetectorTypes());
    }

    public NameVersion determineProjectInformation(UniversalToolsResult universalToolsResult) throws OperationException {
        logger.info(ReportConstants.RUN_SEPARATOR);
        logger.debug("Completed code location tools.");

        logger.debug("Determining project info.");

        NameVersion projectNameVersion = operationRunner.createProjectDecisionOperation(universalToolsResult.getDetectToolProjectInfo());

        logger.info(String.format("Project name: %s", projectNameVersion.getName()));
        logger.info(String.format("Project version: %s", projectNameVersion.getVersion()));

        return projectNameVersion;
    }

}
