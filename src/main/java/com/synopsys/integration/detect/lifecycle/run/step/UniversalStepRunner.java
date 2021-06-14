/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.blackduck.codelocation.upload.UploadTarget;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.run.operation.OperationFactory;
import com.synopsys.integration.detect.tool.DetectableToolResult;
import com.synopsys.integration.detect.tool.UniversalToolsResult;
import com.synopsys.integration.detect.tool.UniversalToolsResultBuilder;
import com.synopsys.integration.detect.tool.detector.DetectorToolResult;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.bdio.AggregateCodeLocation;
import com.synopsys.integration.detect.workflow.bdio.AggregateDecision;
import com.synopsys.integration.detect.workflow.bdio.AggregateMode;
import com.synopsys.integration.detect.workflow.bdio.BdioResult;
import com.synopsys.integration.detect.workflow.codelocation.BdioCodeLocationResult;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocationNamesResult;
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
        if (aggregateDecision.shouldAggregate() && aggregateDecision.getAggregateName().isPresent()) {
            return generateAggregateBdio(aggregateDecision, universalToolsResult, projectNameVersion, aggregateDecision.getAggregateName().get());
        } else {
            return generateStandardBdio(universalToolsResult, projectNameVersion);
        }
    }

    private BdioResult generateAggregateBdio(AggregateDecision aggregateDecision, UniversalToolsResult universalToolsResult, NameVersion projectNameVersion, String aggregateName) throws DetectUserFriendlyException {
        DependencyGraph aggregateDependencyGraph;
        if (aggregateDecision.getAggregateMode() == AggregateMode.DIRECT) {
            aggregateDependencyGraph = operationFactory.aggregateDirect(universalToolsResult.getDetectCodeLocations());
        } else if (aggregateDecision.getAggregateMode() == AggregateMode.TRANSITIVE) {
            aggregateDependencyGraph = operationFactory.aggregateTransitive(universalToolsResult.getDetectCodeLocations());
        } else if (aggregateDecision.getAggregateMode() == AggregateMode.ACCURATE) {
            aggregateDependencyGraph = operationFactory.aggregateAccurate(universalToolsResult.getDetectCodeLocations());
        } else {
            throw new DetectUserFriendlyException(
                String.format("The %s property was set to an unsupported aggregation mode, will not aggregate at this time.", DetectProperties.DETECT_BOM_AGGREGATE_REMEDIATION_MODE.getProperty().getKey()),
                ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        boolean isBdio2 = operationFactory.calculateBdioOptions().isBdio2Enabled();
        String aggregateExtension = isBdio2 ? ".bdio" : ".jsonld";
        AggregateCodeLocation aggregateCodeLocation = operationFactory.createAggregateCodeLocation(aggregateDependencyGraph, projectNameVersion, aggregateName, aggregateExtension);

        if (isBdio2) {
            operationFactory.createAggregateBdio2File(aggregateCodeLocation);
        } else {
            operationFactory.createAggregateBdio1File(aggregateCodeLocation);
        }

        List<UploadTarget> uploadTargets = new ArrayList<>();
        Map<DetectCodeLocation, String> codeLocationNamesResult = new HashMap<>();
        universalToolsResult.getDetectCodeLocations().forEach(cl -> codeLocationNamesResult.put(cl, aggregateCodeLocation.getCodeLocationName())); //TODO: This doesn't seem right, it should just be the aggregate CL name right?
        if (aggregateCodeLocation.getAggregateDependencyGraph().getRootDependencies().size() > 0 || aggregateDecision.shouldUploadEmptyAggregate()) {
            uploadTargets.add(UploadTarget.createDefault(projectNameVersion, aggregateCodeLocation.getCodeLocationName(), aggregateCodeLocation.getAggregateFile()));
        } else {
            logger.warn("The aggregate contained no dependencies, will not upload aggregate at this time.");
        }
        return new BdioResult(uploadTargets, new DetectCodeLocationNamesResult(codeLocationNamesResult), isBdio2);
    }

    private BdioResult generateStandardBdio(UniversalToolsResult universalToolsResult, NameVersion projectNameVersion) throws DetectUserFriendlyException {
        logger.debug("Creating BDIO code locations.");
        BdioCodeLocationResult codeLocationResult = operationFactory.createBdioCodeLocationsFromDetectCodeLocations(universalToolsResult.getDetectCodeLocations(), projectNameVersion);
        DetectCodeLocationNamesResult namesResult = new DetectCodeLocationNamesResult(codeLocationResult.getCodeLocationNames());

        logger.debug("Creating BDIO files from code locations.");
        if (operationFactory.calculateBdioOptions().isBdio2Enabled()) {
            return new BdioResult(operationFactory.createBdio2Files(codeLocationResult, projectNameVersion), namesResult, true);
        } else {
            return new BdioResult(operationFactory.createBdio1Files(codeLocationResult, projectNameVersion), namesResult, false);
        }
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
