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
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryDockerInspectorResolver;
import com.synopsys.integration.detect.tool.detector.inspectors.ArtifactoryGradleInspectorResolver;
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
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapInspectorPaths;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.detectable.inspector.GradleInspectorResolver;
import com.synopsys.integration.detectable.detectable.inspector.PipInspectorResolver;
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
    }

    public DetectableFactory detectableFactory() {
        return new DetectableFactory(fileFinder, executableRunner, externalIdFactory, gson);
    }

    public DetectDetectableFactory detectDetectableFactory() throws DetectUserFriendlyException {
        return new DetectDetectableFactory(detectableFactory(), detectableOptionFactory, detectExecutableResolver, dockerInspectorResolver(), gradleInspectorResolver(), nugetInspectorResolver(detectInfo), pipInspectorResolver());
    }

    private DockerInspectorResolver dockerInspectorResolver() throws DetectUserFriendlyException {
        DockerInspectorInstaller dockerInspectorInstaller = new DockerInspectorInstaller(artifactResolver);
        return new ArtifactoryDockerInspectorResolver(directoryManager, airGapInspectorPaths, fileFinder, dockerInspectorInstaller, detectableOptionFactory.createDockerDetectableOptions());
    }

    private GradleInspectorResolver gradleInspectorResolver() throws DetectUserFriendlyException {
        return new ArtifactoryGradleInspectorResolver(configuration, detectableOptionFactory.createGradleInspectorOptions().getGradleInspectorScriptOptions(), airGapInspectorPaths, directoryManager);
    }

    private NugetInspectorResolver nugetInspectorResolver(DetectInfo detectInfo) throws DetectUserFriendlyException {
        NugetLocatorOptions installerOptions = detectableOptionFactory.createNugetInstallerOptions();
        NugetInspectorLocator locator;
        Optional<File> nugetAirGapPath = airGapInspectorPaths.getNugetInspectorAirGapFile();
        if (nugetAirGapPath.isPresent()) {
            locator = new AirgapNugetInspectorLocator(airGapInspectorPaths);
        } else {
            NugetInspectorInstaller installer = new NugetInspectorInstaller(artifactResolver);
            locator = new OnlineNugetInspectorLocator(installer, directoryManager, installerOptions.getNugetInspectorVersion().orElse(null));
        }

        DotNetRuntimeFinder runtimeFinder = new DotNetRuntimeFinder(executableRunner, detectExecutableResolver, directoryManager.getPermanentDirectory());
        DotNetRuntimeManager dotNetRuntimeManager = new DotNetRuntimeManager(runtimeFinder, new DotNetRuntimeParser());
        return new LocatorNugetInspectorResolver(detectExecutableResolver, executableRunner, detectInfo, fileFinder, installerOptions.getPackagesRepoUrl(), locator, dotNetRuntimeManager);
    }

    private PipInspectorResolver pipInspectorResolver() {
        return new LocalPipInspectorResolver(directoryManager);
    }

    private GradleInspectorScriptCreator gradleInspectorScriptCreator() {
        return new GradleInspectorScriptCreator(configuration);
    }
}
