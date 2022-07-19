package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.lifecycle.run.step.utility.OperationWrapper;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodePublisher;
import com.synopsys.integration.detect.tool.detector.DetectorEventPublisher;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.executable.DirectoryExecutableFinder;
import com.synopsys.integration.detect.tool.detector.executable.SystemPathExecutableFinder;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;
import com.synopsys.integration.detect.workflow.bdio.BdioOptions;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationEventPublisher;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameGenerator;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
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
        ConnectionDetails connectionDetails = detectConfigurationFactory.createConnectionDetails();
        ConnectionFactory connectionFactory = new ConnectionFactory(connectionDetails);
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);
        ArtifactoryZipInstaller artifactoryZipInstaller = new ArtifactoryZipInstaller(artifactResolver);
        AirGapPathFinder airGapPathFinder = new AirGapPathFinder();

        BdioOptions bdioOptions = detectConfigurationFactory.createBdioOptions();
        CodeLocationNameGenerator codeLocationNameGenerator = detectConfigurationFactory.createCodeLocationOverride()
            .map(CodeLocationNameGenerator::withOverride)
            .orElse(CodeLocationNameGenerator.withPrefixSuffix(bdioOptions.getProjectCodeLocationPrefix().orElse(null), bdioOptions.getProjectCodeLocationSuffix().orElse(null)));
        CodeLocationNameManager codeLocationNameManager = new CodeLocationNameManager(codeLocationNameGenerator);
        AirGapInspectorPaths airGapInspectorPaths = new AirGapInspectorPaths(airGapPathFinder);
        DetectExecutableRunner executableRunner = DetectExecutableRunner.newDebug(eventSystem);
        DirectoryExecutableFinder directoryExecutableFinder = DirectoryExecutableFinder.forCurrentOperatingSystem(fileFinder);
        SystemPathExecutableFinder systemExecutableFinder = new SystemPathExecutableFinder(directoryExecutableFinder);
        DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(
            directoryExecutableFinder,
            systemExecutableFinder,
            detectConfigurationFactory.createDetectExecutableOptions()
        );
        OperationSystem operationSystem = new OperationSystem(eventSingletons.getStatusEventPublisher());
        OperationWrapper operationWrapper = new OperationWrapper();

        return new UtilitySingletons(
            externalIdFactory,
            connectionDetails,
            artifactResolver,
            codeLocationNameManager,
            airGapInspectorPaths,
            executableRunner,
            detectExecutableResolver,
            operationSystem,
            operationWrapper,
            artifactoryZipInstaller
        );
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
