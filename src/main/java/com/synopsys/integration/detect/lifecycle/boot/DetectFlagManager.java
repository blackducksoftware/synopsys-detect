package com.synopsys.integration.detect.lifecycle.boot;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.types.path.PathResolver;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.connection.ConnectionDetails;
import com.synopsys.integration.detect.configuration.connection.ConnectionFactory;
import com.synopsys.integration.detect.configuration.enumeration.DetectGroup;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.DetectArgumentStateParser;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonDetector;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonWriter;
import com.synopsys.integration.detect.configuration.help.print.HelpPrinter;
import com.synopsys.integration.detect.tool.detector.DetectDetectableFactory;
import com.synopsys.integration.detect.tool.detector.DetectorRuleFactory;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableResolver;
import com.synopsys.integration.detect.tool.detector.executable.DetectExecutableRunner;
import com.synopsys.integration.detect.tool.detector.executable.DirectoryExecutableFinder;
import com.synopsys.integration.detect.tool.detector.executable.SystemPathExecutableFinder;
import com.synopsys.integration.detect.tool.detector.inspectors.DockerInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.GradleInspectorInstaller;
import com.synopsys.integration.detect.tool.detector.inspectors.nuget.NugetInspectorInstaller;
import com.synopsys.integration.detect.util.filter.DetectFilter;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.AirGapPathFinder;
import com.synopsys.integration.detect.workflow.airgap.DockerAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.GradleAirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.NugetAirGapCreator;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.detectable.annotation.DetectableInfo;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.WildcardFileFinder;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

import freemarker.template.Configuration;

public class DetectFlagManager {
    private DetectArgumentState detectArgumentState;

    public DetectFlagManager(String[] sourceArgs) {
        detectArgumentState = parseDetectArgumentState(sourceArgs);
    }

    public void printAppropriateHelp(List<Property> properties, DetectArgumentState detectArgumentState) {
        HelpPrinter helpPrinter = new HelpPrinter();
        helpPrinter.printAppropriateHelpMessage(System.out, properties, Arrays.asList(DetectGroup.values()), DetectGroup.BLACKDUCK_SERVER, detectArgumentState);
    }

    public void printHelpJsonDocument(List<Property> properties, DetectInfo detectInfo, Gson gson) {
        DetectorRuleFactory ruleFactory = new DetectorRuleFactory();
        // TODO: Is there a better way to build a fake set of rules?
        DetectDetectableFactory mockFactory = new DetectDetectableFactory(null, null, null, null, null, null, null);
        DetectorRuleSet build = ruleFactory.createRules(mockFactory, false);
        DetectorRuleSet buildless = ruleFactory.createRules(mockFactory, true);
        List<HelpJsonDetector> buildDetectors = build.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, build)).collect(Collectors.toList());
        List<HelpJsonDetector> buildlessDetectors = buildless.getOrderedDetectorRules().stream().map(detectorRule -> convertDetectorRule(detectorRule, buildless)).collect(Collectors.toList());

        HelpJsonWriter helpJsonWriter = new HelpJsonWriter(gson);
        helpJsonWriter.writeGsonDocument(String.format("synopsys-detect-%s-help.json", detectInfo.getDetectVersion()), properties, buildDetectors, buildlessDetectors);
    }

    public HelpJsonDetector convertDetectorRule(DetectorRule rule, DetectorRuleSet ruleSet) {
        HelpJsonDetector helpData = new HelpJsonDetector();
        helpData.setDetectorName(rule.getName());
        helpData.setDetectorDescriptiveName(rule.getDescriptiveName());
        helpData.setDetectorType(rule.getDetectorType().toString());
        helpData.setMaxDepth(rule.getMaxDepth());
        helpData.setNestable(rule.isNestable());
        helpData.setNestInvisible(rule.isNestInvisible());
        helpData.setYieldsTo(ruleSet.getYieldsTo(rule).stream().map(DetectorRule::getDescriptiveName).collect(Collectors.toList()));
        helpData.setFallbackTo(ruleSet.getFallbackFrom(rule).map(DetectorRule::getDescriptiveName).orElse(""));

        //Attempt to create the detectable.
        //Not currently possible. Need a full DetectableConfiguration to be able to make Detectables.
        Class<Detectable> detectableClass = rule.getDetectableClass();
        Optional<DetectableInfo> infoSearch = Arrays.stream(detectableClass.getAnnotations())
                                                  .filter(annotation -> annotation instanceof DetectableInfo)
                                                  .map(annotation -> (DetectableInfo) annotation)
                                                  .findFirst();

        if (infoSearch.isPresent()) {
            DetectableInfo info = infoSearch.get();
            helpData.setDetectableLanguage(info.language());
            helpData.setDetectableRequirementsMarkdown(info.requirementsMarkdown());
            helpData.setDetectableForge(info.forge());
        }

        return helpData;
    }

    public DetectArgumentState parseDetectArgumentState(String[] sourceArgs) {
        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        return detectArgumentState;
    }

    public File createAirGapZip(DetectFilter inspectorFilter, PropertyConfiguration detectConfiguration, PathResolver pathResolver, DirectoryManager directoryManager, Gson gson,
        EventSystem eventSystem,
        Configuration configuration,
        String airGapSuffix)
        throws DetectUserFriendlyException {
        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, pathResolver);
        ConnectionDetails connectionDetails = detectConfigurationFactory.createConnectionDetails();
        ConnectionFactory connectionFactory = new ConnectionFactory(connectionDetails);
        ArtifactResolver artifactResolver = new ArtifactResolver(connectionFactory, gson);

        FileFinder fileFinder = new WildcardFileFinder();
        DirectoryExecutableFinder directoryExecutableFinder = DirectoryExecutableFinder.forCurrentOperatingSystem(fileFinder);
        SystemPathExecutableFinder systemPathExecutableFinder = new SystemPathExecutableFinder(directoryExecutableFinder);
        DetectExecutableResolver detectExecutableResolver = new DetectExecutableResolver(directoryExecutableFinder, systemPathExecutableFinder, detectConfigurationFactory.createExecutablePaths());

        GradleInspectorInstaller gradleInspectorInstaller = new GradleInspectorInstaller(artifactResolver);
        DetectExecutableRunner runner = DetectExecutableRunner.newDebug(eventSystem);
        GradleAirGapCreator gradleAirGapCreator = new GradleAirGapCreator(detectExecutableResolver, gradleInspectorInstaller, runner, configuration);

        NugetAirGapCreator nugetAirGapCreator = new NugetAirGapCreator(new NugetInspectorInstaller(artifactResolver));
        DockerAirGapCreator dockerAirGapCreator = new DockerAirGapCreator(new DockerInspectorInstaller(artifactResolver));

        AirGapCreator airGapCreator = new AirGapCreator(new AirGapPathFinder(), eventSystem, gradleAirGapCreator, nugetAirGapCreator, dockerAirGapCreator);
        String gradleInspectorVersion = detectConfiguration.getValueOrEmpty(DetectProperties.DETECT_GRADLE_INSPECTOR_VERSION.getProperty()).orElse(null);
        return airGapCreator.createAirGapZip(inspectorFilter, directoryManager.getRunHomeDirectory(), airGapSuffix, gradleInspectorVersion);
    }

    public DetectArgumentState getDetectArgumentState() {
        return detectArgumentState;
    }
}
