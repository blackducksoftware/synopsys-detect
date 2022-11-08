package com.synopsys.integration.detect.tool.detector.factory;

import java.io.File;
import java.util.Optional;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.lifecycle.run.singleton.UtilitySingletons;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryDockerInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryGradleInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.LocalPipInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.AirgapNugetInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.ArtifactoryNugetInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.NugetInspectorExecutableLocator;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.OnlineNugetInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.AirgapProjectInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.OnlineProjectInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer.ArtifactoryProjectInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer.LocalProjectInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer.ProjectInspectorInstaller;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;
import com.synopsys.integration.detectable.detectables.projectinspector.ProjectInspectorOptions;
import com.synopsys.integration.detectable.factory.DetectableFactory;

import freemarker.template.Configuration;

public class DetectorFactory {
    private final FileFinder fileFinder;
    private final DetectInfo detectInfo;
    private final DetectExecutableRunner executableRunner;
    private final ExternalIdFactory externalIdFactory;
    private final Gson gson;
    private final Configuration configuration;
    private final DetectableOptionFactory detectableOptionFactory;
    private final DetectExecutableResolver detectExecutableResolver;
    private final DirectoryManager directoryManager;
    private final AirGapInspectorPaths airGapInspectorPaths;
    private final ArtifactResolver artifactResolver;
    private final ArtifactoryZipInstaller artifactoryZipInstaller;
    private final InstalledToolManager installedToolManager;
    private final InstalledToolLocator installedToolLocator;

    public DetectorFactory(BootSingletons bootSingletons, UtilitySingletons utilitySingletons) {
        gson = bootSingletons.getGson();
        detectInfo = bootSingletons.getDetectInfo();
        configuration = bootSingletons.getConfiguration();
        fileFinder = bootSingletons.getFileFinder();
        detectableOptionFactory = bootSingletons.getDetectableOptionFactory();
        directoryManager = bootSingletons.getDirectoryManager();

        detectExecutableResolver = utilitySingletons.getDetectExecutableResolver();
        executableRunner = utilitySingletons.getExecutableRunner();
        externalIdFactory = utilitySingletons.getExternalIdFactory();
        airGapInspectorPaths = utilitySingletons.getAirGapInspectorPaths();
        artifactResolver = utilitySingletons.getArtifactResolver();
        artifactoryZipInstaller = utilitySingletons.getArtifactoryZipInstaller();
        this.installedToolManager = bootSingletons.getInstalledToolManager();
        this.installedToolLocator = bootSingletons.getInstalledToolLocator();
    }

    public DetectableFactory detectableFactory() {
        return new DetectableFactory(fileFinder, executableRunner, externalIdFactory, gson);
    }

    public DetectDetectableFactory detectDetectableFactory() throws DetectUserFriendlyException {
        return new DetectDetectableFactory(
            detectableFactory(),
            detectableOptionFactory,
            detectExecutableResolver,
            dockerInspectorResolver(),
            gradleInspectorResolver(),
            nugetInspectorResolver(detectInfo),
            pipInspectorResolver(),
            projectInspectorResolver(detectInfo, detectableOptionFactory.createProjectInspectorOptions())
        );
    }

    private DockerInspectorResolver dockerInspectorResolver() throws DetectUserFriendlyException {
        DockerInspectorInstaller dockerInspectorInstaller = new DockerInspectorInstaller(artifactResolver);
        return new ArtifactoryDockerInspectorResolver(
            directoryManager,
            airGapInspectorPaths,
            fileFinder,
            dockerInspectorInstaller,
            detectableOptionFactory.createDockerDetectableOptions(),
            installedToolManager,
            installedToolLocator
        );
    }

    private GradleInspectorResolver gradleInspectorResolver() throws DetectUserFriendlyException {
        return new ArtifactoryGradleInspectorResolver(
            configuration,
            detectableOptionFactory.createGradleInspectorOptions().getGradleInspectorScriptOptions(),
            airGapInspectorPaths,
            directoryManager
        );
    }

    private NugetInspectorResolver nugetInspectorResolver(DetectInfo detectInfo) throws DetectUserFriendlyException {
        NugetInspectorResolver resolver;
        Optional<File> nugetAirGapPath = airGapInspectorPaths.getNugetInspectorAirGapFile();
        NugetInspectorExecutableLocator executableLocator = new NugetInspectorExecutableLocator(detectInfo);
        if (nugetAirGapPath.isPresent()) {
            resolver = new AirgapNugetInspectorResolver(airGapInspectorPaths, executableLocator, detectInfo);
        } else {
            ArtifactoryNugetInspectorInstaller installer = new ArtifactoryNugetInspectorInstaller(detectInfo, artifactoryZipInstaller, executableLocator);
            resolver = new OnlineNugetInspectorResolver(
                installer,
                directoryManager,
                installedToolManager,
                installedToolLocator
            );
        }
        return resolver;
    }

    private ProjectInspectorResolver projectInspectorResolver(DetectInfo detectInfo, ProjectInspectorOptions projectInspectorOptions) {
        ProjectInspectorExecutableLocator projectInspectorExecutableLocator = new ProjectInspectorExecutableLocator(detectInfo);
        Optional<File> projectInspectorAirgapPath = airGapInspectorPaths.getProjectInspectorAirGapFile();
        if (projectInspectorAirgapPath.isPresent()) {
            return new AirgapProjectInspectorResolver(airGapInspectorPaths, projectInspectorExecutableLocator, detectInfo);
        } else {
            ProjectInspectorInstaller projectInspectorInstaller;
            if (projectInspectorOptions.getProjectInspectorZipPath().isPresent()) {
                projectInspectorInstaller = new LocalProjectInspectorInstaller(
                    projectInspectorExecutableLocator,
                    projectInspectorOptions.getProjectInspectorZipPath().get()
                );
            } else {
                projectInspectorInstaller = new ArtifactoryProjectInspectorInstaller(
                    detectInfo,
                    artifactoryZipInstaller,
                    projectInspectorExecutableLocator
                );
            }
            return new OnlineProjectInspectorResolver(
                projectInspectorInstaller,
                directoryManager,
                installedToolManager,
                installedToolLocator
            );
        }
    }

    private PipInspectorResolver pipInspectorResolver() {
        return new LocalPipInspectorResolver(directoryManager);
    }

    private GradleInspectorScriptCreator gradleInspectorScriptCreator() {
        return new GradleInspectorScriptCreator(configuration);
    }
}
