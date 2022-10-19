package com.synopsys.integration.detect.lifecycle.boot;

import java.io.IOException;
import java.util.List;
import java.util.SortedMap;

import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.property.types.path.TildeInPathResolver;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
import com.synopsys.integration.detect.configuration.validation.DetectConfigurationBootManager;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.interactive.InteractiveModeDecisionTree;
import com.synopsys.integration.detect.interactive.InteractivePropertySourceBuilder;
import com.synopsys.integration.detect.interactive.InteractiveWriter;
import com.synopsys.integration.detect.lifecycle.boot.product.BlackDuckConnectivityChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootFactory;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBootOptions;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckMinimumVersionChecks;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionChecker;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionParser;
import com.synopsys.integration.detect.lifecycle.boot.product.version.BlackDuckVersionSensitiveOptions;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableOptions;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.executable.DirectoryExecutableFinder;
import com.synopsys.integration.detect.tool.detector.executable.SystemPathExecutableFinder;
import com.synopsys.integration.detect.tool.detector.inspector.ArtifactoryZipInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.ArtifactoryNugetInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspector.nuget.NugetInspectorExecutableLocator;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.ProjectInspectorExecutableLocator;
import com.synopsys.integration.detect.tool.detector.inspector.projectinspector.installer.ArtifactoryProjectInspectorInstaller;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.DetectRunId;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;
import com.synopsys.integration.detect.workflow.airgap.DetectFontAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.DockerAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.GradleAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.NugetInspectorAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.ProjectInspectorAirGapCreator;
import com.synopsys.integration.detect.workflow.blackduck.analytics.AnalyticsConfigurationService;
import com.synopsys.integration.detect.workflow.blackduck.font.DetectFontInstaller;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detect.workflow.profiling.DetectorProfiler;
import com.synopsys.integration.util.OperatingSystemType;

import freemarker.template.Configuration;

//Responsible for creating a few classes boot needs
public class DetectBootFactory {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DetectRunId detectRunId;
    private final DetectInfo detectInfo;
    private final Gson gson;
    private final EventSystem eventSystem;
    private final BlackDuckConnectivityChecker blackDuckConnectivityChecker;
    private final FileFinder fileFinder;

    public DetectBootFactory(DetectRunId detectRunId, DetectInfo detectInfo, Gson gson, EventSystem eventSystem, FileFinder fileFinder) {
        this.detectRunId = detectRunId;
        this.detectInfo = detectInfo;
        this.gson = gson;
        this.eventSystem = eventSystem;
        this.blackDuckConnectivityChecker = new BlackDuckConnectivityChecker();
        this.fileFinder = fileFinder;
    }

    public BootSingletons createRunDependencies(
        ProductRunData productRunData,
        PropertyConfiguration detectConfiguration,
        DetectableOptionFactory detectableOptionFactory,
        DetectConfigurationFactory detectConfigurationFactory,
        DirectoryManager directoryManager,
        Configuration configuration,
        InstalledToolManager installedToolManager,
        InstalledToolLocator installedToolLocator
    ) {
        return new BootSingletons(
            productRunData,
            detectRunId,
            gson,
            detectInfo,
            fileFinder,
            eventSystem,
            createDetectorProfiler(),
            detectConfiguration,
            detectableOptionFactory,
            detectConfigurationFactory,
            directoryManager,
            configuration,
            installedToolManager,
            installedToolLocator
        );
    }

    public Configuration createFreemarkerConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(Application.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);

        return configuration;
    }

    public PathResolver createPathResolver() {
        PathResolver pathResolver;

        if (detectInfo.getCurrentOs() != OperatingSystemType.WINDOWS) {
            logger.info("Tilde's will be automatically resolved to USER HOME.");
            pathResolver = new TildeInPathResolver(SystemUtils.USER_HOME);
        } else {
            pathResolver = new SimplePathResolver();
        }

        return pathResolver;
    }

    public DiagnosticSystem createDiagnosticSystem(
        PropertyConfiguration detectConfiguration,
        DirectoryManager directoryManager,
        SortedMap<String, String> maskedRawPropertyValues
    ) {
        return new DiagnosticSystem(detectConfiguration, detectRunId, detectInfo, directoryManager, eventSystem, maskedRawPropertyValues);
    }

    public AirGapCreator createAirGapCreator(
        ConnectionDetails connectionDetails, DetectExecutableOptions detectExecutableOptions, Configuration freemarkerConfiguration, InstalledToolManager installedToolManager,
        InstalledToolLocator installedToolLocator
    ) {
        ConnectionFactory connectionFactory = new ConnectionFactory(connectionDetails);
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);
        ArtifactoryZipInstaller artifactoryZipInstaller = new ArtifactoryZipInstaller(artifactResolver);

        DirectoryExecutableFinder directoryExecutableFinder = DirectoryExecutableFinder.forCurrentOperatingSystem(fileFinder);
        SystemPathExecutableFinder systemPathExecutableFinder = new SystemPathExecutableFinder(directoryExecutableFinder);
        DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(directoryExecutableFinder, systemPathExecutableFinder, detectExecutableOptions);

        DetectExecutableRunner runner = DetectExecutableRunner.newDebug(eventSystem);
        GradleAirGapCreator gradleAirGapCreator = new GradleAirGapCreator(detectExecutableResolver, runner, freemarkerConfiguration);

        NugetInspectorAirGapCreator nugetAirGapCreator = new NugetInspectorAirGapCreator(new ArtifactoryNugetInspectorInstaller(
            detectInfo,
            artifactoryZipInstaller,
            new NugetInspectorExecutableLocator(detectInfo)
        ));

        ProjectInspectorExecutableLocator projectInspectorExecutableLocator = new ProjectInspectorExecutableLocator(detectInfo);
        ArtifactoryProjectInspectorInstaller projectInspectorInstaller = new ArtifactoryProjectInspectorInstaller(
            detectInfo,
            artifactoryZipInstaller,
            projectInspectorExecutableLocator
        );
        ProjectInspectorAirGapCreator projectInspectorAirGapCreator = new ProjectInspectorAirGapCreator(projectInspectorInstaller);

        DockerAirGapCreator dockerAirGapCreator = new DockerAirGapCreator(new DockerInspectorInstaller(artifactResolver));
        DetectFontAirGapCreator detectFontAirGapCreator = new DetectFontAirGapCreator(new DetectFontInstaller(artifactResolver, installedToolManager, installedToolLocator));

        return new AirGapCreator(
            new AirGapPathFinder(),
            eventSystem,
            gradleAirGapCreator,
            nugetAirGapCreator,
            dockerAirGapCreator,
            detectFontAirGapCreator,
            projectInspectorAirGapCreator
        );
    }

    public HelpJsonManager createHelpJsonManager() {
        return new HelpJsonManager(gson);
    }

    public DirectoryManager createDirectoryManager(DetectConfigurationFactory detectConfigurationFactory) throws IOException {
        return new DirectoryManager(detectConfigurationFactory.createDirectoryOptions(), detectRunId);
    }

    public DetectConfigurationBootManager createDetectConfigurationBootManager(PropertyConfiguration detectConfiguration) {
        PropertyConfigurationHelpContext detectConfigurationReporter = new PropertyConfigurationHelpContext(detectConfiguration);
        return new DetectConfigurationBootManager(eventSystem, detectConfigurationReporter);
    }

    private DetectorProfiler createDetectorProfiler() {
        return new DetectorProfiler(eventSystem);
    }

    public ProductBootFactory createProductBootFactory(DetectConfigurationFactory detectConfigurationFactory) {
        return new ProductBootFactory(detectInfo, eventSystem, detectConfigurationFactory);
    }

    public ProductBoot createProductBoot(DetectConfigurationFactory detectConfigurationFactory, DetectToolFilter detectToolFilter, BlackduckScanMode blackduckScanMode) {
        BlackDuckVersionChecker blackDuckVersionChecker = new BlackDuckVersionChecker(new BlackDuckVersionParser(),
            new BlackDuckMinimumVersionChecks(), new BlackDuckVersionSensitiveOptions(detectToolFilter, blackduckScanMode)
        );

        ProductBootOptions productBootOptions = detectConfigurationFactory.createProductBootOptions();
        return new ProductBoot(
            blackDuckConnectivityChecker,
            createAnalyticsConfigurationService(),
            createProductBootFactory(detectConfigurationFactory),
            productBootOptions,
            blackDuckVersionChecker
        );
    }

    public AnalyticsConfigurationService createAnalyticsConfigurationService() {
        return new AnalyticsConfigurationService();
    }

    public InteractiveManager createInteractiveManager(List<PropertySource> propertySources) {
        InteractiveWriter writer = InteractiveWriter.defaultWriter(System.console(), System.in, System.out);
        InteractivePropertySourceBuilder propertySourceBuilder = new InteractivePropertySourceBuilder(writer);
        InteractiveModeDecisionTree interactiveModeDecisionTree = new InteractiveModeDecisionTree(detectInfo, blackDuckConnectivityChecker, propertySources, gson);
        return new InteractiveManager(propertySourceBuilder, writer, interactiveModeDecisionTree);
    }

}
