/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.tool.detector.CodeLocationConverter;
import com.synopsys.integration.detect.tool.detector.extraction.ExtractionEnvironmentProvider;
import com.synopsys.integration.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.project.DetectToolProjectInfo;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;
import com.synopsys.integration.detect.workflow.status.Status;
import com.synopsys.integration.detect.workflow.status.StatusType;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.ExceptionDetectableResult;
import com.synopsys.integration.detectable.detectables.docker.DockerExtractor;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detector.base.DetectableCreatable;
import com.synopsys.integration.util.NameVersion;

public class DetectableTool {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectableCreatable detectableCreatable;
    private final ExtractionEnvironmentProvider extractionEnvironmentProvider;
    private final CodeLocationConverter codeLocationConverter;
    private final String name;
    private final DetectTool detectTool;
    private final EventSystem eventSystem;

    public DetectableTool(final DetectableCreatable detectableCreatable, final ExtractionEnvironmentProvider extractionEnvironmentProvider, final CodeLocationConverter codeLocationConverter,
        final String name, final DetectTool detectTool, final EventSystem eventSystem) {
        this.codeLocationConverter = codeLocationConverter;
        this.name = name;
        this.detectableCreatable = detectableCreatable;
        this.extractionEnvironmentProvider = extractionEnvironmentProvider;
        this.detectTool = detectTool;
        this.eventSystem = eventSystem;
    }

    public DetectableToolResult execute(final File sourcePath) { //TODO: Caller publishes result.
        logger.trace("Starting a detectable tool.");

        final DetectableEnvironment detectableEnvironment = new DetectableEnvironment(sourcePath);
        final Detectable detectable = detectableCreatable.createDetectable(detectableEnvironment);

        //TODO: Replicate? logger.info(String.format("Initializing %s.", detectable.getDescriptiveName()));

        final DetectableResult applicable = detectable.applicable();

        if (!applicable.getPassed()) {
            logger.debug("Was not applicable.");
            return DetectableToolResult.skip();
        }

        logger.debug("Applicable passed.");

        DetectableResult extractable;
        try {
            extractable = detectable.extractable();
        } catch (final DetectableException e) {
            extractable = new ExceptionDetectableResult(e);
        }

        if (!extractable.getPassed()) {
            logger.error("Was not extractable: " + extractable.toDescription());
            eventSystem.publishEvent(Event.StatusSummary, new Status(name, StatusType.FAILURE));
            eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.DETECTOR, Arrays.asList(extractable.toDescription())));
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_GENERAL_ERROR, extractable.toDescription()));
            return DetectableToolResult.failed(extractable);
        }

        logger.debug("Extractable passed.");

        final ExtractionEnvironment extractionEnvironment = extractionEnvironmentProvider.createExtractionEnvironment(name);
        Extraction extraction;
        try {
            extraction = detectable.extract(extractionEnvironment);
        } catch (ExecutableFailedException e) {
            extraction = Extraction.fromFailedExecutable(e);
        }

        if (!extraction.isSuccess()) {
            logger.error("Extraction was not success.");
            eventSystem.publishEvent(Event.StatusSummary, new Status(name, StatusType.FAILURE));
            eventSystem.publishEvent(Event.Issue, new DetectIssue(DetectIssueType.DETECTOR, Arrays.asList(extraction.getDescription())));
            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_GENERAL_ERROR, extractable.toDescription()));
            return DetectableToolResult.failed();
        } else {
            logger.debug("Extraction success.");
            eventSystem.publishEvent(Event.StatusSummary, new Status(name, StatusType.SUCCESS));
        }

        final Map<CodeLocation, DetectCodeLocation> detectCodeLocationMap = codeLocationConverter.toDetectCodeLocation(sourcePath, extraction, sourcePath, name);
        final List<DetectCodeLocation> detectCodeLocations = new ArrayList<>(detectCodeLocationMap.values());

        // new DetectableToolResult

        final File dockerTar = extraction.getMetaData(DockerExtractor.DOCKER_TAR_META_DATA).orElse(null); // ifPresent(DetectableToolResult::addDockerTar)

        DetectToolProjectInfo projectInfo = null;
        if (StringUtils.isNotBlank(extraction.getProjectName()) || StringUtils.isNotBlank(extraction.getProjectVersion())) {
            final NameVersion nameVersion = new NameVersion(extraction.getProjectName(), extraction.getProjectVersion());
            projectInfo = new DetectToolProjectInfo(detectTool, nameVersion);
        }

        logger.debug("Tool finished.");

        return DetectableToolResult.success(detectCodeLocations, projectInfo, dockerTar);
    }
}
