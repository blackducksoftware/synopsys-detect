package com.synopsys.integration.detect.lifecycle.run.singleton;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.lifecycle.run.step.utility.OperationWrapper;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.codelocation.CodeLocationNameManager;
import com.synopsys.integration.detect.workflow.status.OperationSystem;

public class UtilitySingletons {
    private final ExternalIdFactory externalIdFactory;
    private final ConnectionDetails connectionDetails;
    private final ArtifactResolver artifactResolver;
    private final ArtifactoryZipInstaller artifactoryZipInstaller;
    private final CodeLocationNameManager codeLocationNameManager;
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final DetectExecutableRunner executableRunner;
    private final DetectExecutableResolver detectExecutableResolver;
    private final OperationSystem operationSystem;
    private final OperationWrapper operationWrapper;

    public UtilitySingletons(
        ExternalIdFactory externalIdFactory,
        ConnectionDetails connectionDetails,
        ArtifactResolver artifactResolver,
        CodeLocationNameManager codeLocationNameManager,
        AirGapInspectorPaths airGapInspectorPaths,
        DetectExecutableRunner executableRunner,
        DetectExecutableResolver detectExecutableResolver,
        OperationSystem operationSystem,
        OperationWrapper operationWrapper,
        ArtifactoryZipInstaller artifactoryZipInstaller
    ) {
        this.externalIdFactory = externalIdFactory;
        this.connectionDetails = connectionDetails;
        this.artifactResolver = artifactResolver;
        this.codeLocationNameManager = codeLocationNameManager;
        this.airGapInspectorPaths = airGapInspectorPaths;
        this.executableRunner = executableRunner;
        this.detectExecutableResolver = detectExecutableResolver;
        this.operationSystem = operationSystem;
        this.operationWrapper = operationWrapper;
        this.artifactoryZipInstaller = artifactoryZipInstaller;
    }

    public ExternalIdFactory getExternalIdFactory() {
        return externalIdFactory;
    }

    public ConnectionDetails getConnectionDetails() {
        return connectionDetails;
    }

    public ArtifactResolver getArtifactResolver() {
        return artifactResolver;
    }

    public CodeLocationNameManager getCodeLocationNameManager() {
        return codeLocationNameManager;
    }

    public AirGapInspectorPaths getAirGapInspectorPaths() {
        return airGapInspectorPaths;
    }

    public DetectExecutableRunner getExecutableRunner() {
        return executableRunner;
    }

    public DetectExecutableResolver getDetectExecutableResolver() {
        return detectExecutableResolver;
    }

    public OperationSystem getOperationSystem() {
        return operationSystem;
    }

    public OperationWrapper getOperationWrapper() {
        return operationWrapper;
    }

    public ArtifactoryZipInstaller getArtifactoryZipInstaller() {
        return artifactoryZipInstaller;
    }
}
