/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.BdioTransformer;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.executable.DirectoryExecutableFinder;
import com.synopsys.integration.detect.tool.detector.executable.SystemPathExecutableFinder;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.codelocation.CreateBdioCodeLocationsFromDetectCodeLocationsOperation;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.project.ProjectEventPublisher;
import com.synopsys.integration.detect.workflow.status.OperationSystem;
import com.synopsys.integration.detect.workflow.status.StatusEventPublisher;

public class SingletonFactory {
    private final Gson gson;
    private final EventSystem eventSystem;
    private final FileFinder fileFinder;
    private final DirectoryManager directoryManager;
    private final DetectConfigurationFactory detectConfigurationFactory;

    public SingletonFactory(BootSingletons bootSingletons) {
        this.gson = bootSingletons.getGson();
        this.eventSystem = bootSingletons.getEventSystem();
        this.fileFinder = bootSingletons.getFileFinder();
        this.directoryManager = bootSingletons.getDirectoryManager();
        this.detectConfigurationFactory = bootSingletons.getDetectConfigurationFactory();
    }

    public UtilitySingletons createUtilitySingletons(EventSingletons eventSingletons) throws DetectUserFriendlyException {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        ConnectionFactory connectionFactory = new ConnectionFactory(detectConfigurationFactory.createConnectionDetails());
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);
        AirGapPathFinder airGapPathFinder = new AirGapPathFinder();
        CodeLocationNameGenerator codeLocationNameGenerator = new CodeLocationNameGenerator(detectConfigurationFactory.createCodeLocationOverride());
        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(codeLocationNameGenerator);
        CreateBdioCodeLocationsFromDetectCodeLocationsOperation createBdioCodeLocationsFromDetectCodeLocationsOperation = new CreateBdioCodeLocationsFromDetectCodeLocationsOperation(codeLocationNameManager, directoryManager);
        AirGapInspectorPaths airGapInspectorPaths = new AirGapInspectorPaths(airGapPathFinder, detectConfigurationFactory.createAirGapOptions());
        BdioTransformer bdioTransformer = new BdioTransformer();
        DetectExecutableRunner executableRunner = DetectExecutableRunner.newDebug(eventSystem);
        DirectoryExecutableFinder directoryExecutableFinder = DirectoryExecutableFinder.forCurrentOperatingSystem(fileFinder);
        SystemPathExecutableFinder systemExecutableFinder = new SystemPathExecutableFinder(directoryExecutableFinder);
        DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(directoryExecutableFinder, systemExecutableFinder, detectConfigurationFactory.createDetectExecutableOptions());
        OperationSystem operationSystem = new OperationSystem(eventSingletons.getStatusEventPublisher());

        return new UtilitySingletons(externalIdFactory, connectionFactory, artifactResolver, codeLocationNameManager, createBdioCodeLocationsFromDetectCodeLocationsOperation, airGapInspectorPaths, bdioTransformer,
            executableRunner, detectExecutableResolver, operationSystem);
    }

    public EventSingletons createEventSingletons() {
        StatusEventPublisher statusEventPublisher = new StatusEventPublisher(eventSystem);
        ExitCodePublisher exitCodePublisher = new ExitCodePublisher(eventSystem);
        DetectorEventPublisher detectorEventPublisher = new DetectorEventPublisher(eventSystem);
        CodeLocationEventPublisher codeLocationEventPublisher = new CodeLocationEventPublisher(eventSystem);
        ProjectEventPublisher projectEventPublisher = new ProjectEventPublisher(eventSystem);
        return new EventSingletons(statusEventPublisher, exitCodePublisher, detectorEventPublisher, codeLocationEventPublisher, projectEventPublisher);
    }
}
