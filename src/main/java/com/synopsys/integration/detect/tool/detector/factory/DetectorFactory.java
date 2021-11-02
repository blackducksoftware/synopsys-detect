/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
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
import com.synopsys.integration.detect.tool.cache.CachedToolInstaller;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryDockerInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryGradleInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.LocalPipInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.AirgapNugetInspectorLocator;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.LocatorNugetInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetInspectorLocator;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetLocatorOptions;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.OnlineNugetInspectorLocator;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeFinder;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeManager;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.runtime.DotNetRuntimeParser;
import com.synopsys.integration.detect.tool.detector.inspectors.projectinspector.AirgapProjectInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.projectinspector.ArtifactoryProjectInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.projectinspector.OnlineProjectInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.ProjectInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.nuget.NugetInspectorResolver;
import com.synopsys.integration.detectable.detectables.docker.DockerInspectorResolver;
import com.synopsys.integration.detectable.detectables.gradle.inspection.inspector.GradleInspectorScriptCreator;
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
    private final EventSystem eventSystem;
    private final CachedToolInstaller cachedToolInstaller;

    public DetectorFactory(BootSingletons bootSingletons, UtilitySingletons utilitySingletons, EventSystem eventSystem) {
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
        this.eventSystem = eventSystem;
        this.cachedToolInstaller = bootSingletons.getCachedToolInstaller();
    }

    public DetectableFactory detectableFactory() {
        return new DetectableFactory(fileFinder, executableRunner, externalIdFactory, gson);
    }

    public DetectDetectableFactory detectDetectableFactory() throws DetectUserFriendlyException {
        return new DetectDetectableFactory(detectableFactory(), detectableOptionFactory, detectExecutableResolver, dockerInspectorResolver(), gradleInspectorResolver(), nugetInspectorResolver(detectInfo), pipInspectorResolver(),
            projectInspectorResolver(detectInfo));
    }

    private DockerInspectorResolver dockerInspectorResolver() throws DetectUserFriendlyException {
        DockerInspectorInstaller dockerInspectorInstaller = new DockerInspectorInstaller(artifactResolver, eventSystem);
        return new ArtifactoryDockerInspectorResolver(directoryManager, airGapInspectorPaths, fileFinder, dockerInspectorInstaller, detectableOptionFactory.createDockerDetectableOptions(), cachedToolInstaller);
    }

    private GradleInspectorResolver gradleInspectorResolver() throws DetectUserFriendlyException {
        return new ArtifactoryGradleInspectorResolver(configuration, detectableOptionFactory.createGradleInspectorOptions().getGradleInspectorScriptOptions(), airGapInspectorPaths, directoryManager, cachedToolInstaller);
    }

    private NugetInspectorResolver nugetInspectorResolver(DetectInfo detectInfo) throws DetectUserFriendlyException {
        NugetLocatorOptions installerOptions = detectableOptionFactory.createNugetInstallerOptions();
        NugetInspectorLocator locator;
        Optional<File> nugetAirGapPath = airGapInspectorPaths.getNugetInspectorAirGapFile();
        if (nugetAirGapPath.isPresent()) {
            locator = new AirgapNugetInspectorLocator(airGapInspectorPaths);
        } else {
            NugetInspectorInstaller installer = new NugetInspectorInstaller(artifactoryZipInstaller, eventSystem);
            locator = new OnlineNugetInspectorLocator(installer, directoryManager, installerOptions.getNugetInspectorVersion().orElse(null), cachedToolInstaller);
        }

        DotNetRuntimeFinder runtimeFinder = new DotNetRuntimeFinder(executableRunner, detectExecutableResolver, directoryManager.getPermanentDirectory());
        DotNetRuntimeManager dotNetRuntimeManager = new DotNetRuntimeManager(runtimeFinder, new DotNetRuntimeParser());
        return new LocatorNugetInspectorResolver(detectExecutableResolver, executableRunner, detectInfo, fileFinder, installerOptions.getPackagesRepoUrl(), locator, dotNetRuntimeManager);
    }

    private ProjectInspectorResolver projectInspectorResolver(DetectInfo detectInfo) {
        ProjectInspectorExecutableLocator projectInspectorExecutableLocator = new ProjectInspectorExecutableLocator(detectInfo);

        Optional<File> projectInspectorAirgapPath = airGapInspectorPaths.getProjectInspectorAirGapFile();
        if (projectInspectorAirgapPath.isPresent()) {
            return new AirgapProjectInspectorResolver(airGapInspectorPaths, projectInspectorExecutableLocator, detectInfo);
        } else {
            ArtifactoryProjectInspectorInstaller artifactoryProjectInspectorInstaller = new ArtifactoryProjectInspectorInstaller(detectInfo, artifactoryZipInstaller, projectInspectorExecutableLocator, eventSystem, cachedToolInstaller);
            return new OnlineProjectInspectorResolver(artifactoryProjectInspectorInstaller, directoryManager);
        }
    }

    private PipInspectorResolver pipInspectorResolver() {
        return new LocalPipInspectorResolver(directoryManager);
    }

    private GradleInspectorScriptCreator gradleInspectorScriptCreator() {
        return new GradleInspectorScriptCreator(configuration);
    }
}
