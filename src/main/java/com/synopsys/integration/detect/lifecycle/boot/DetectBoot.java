package com.synopsys.integration.detect.lifecycle.boot;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Collections;

import com.synopsys.integration.configuration.property.types.enumallnone.list.AllEnumList;
import com.synopsys.integration.detect.configuration.connection.BlackDuckConnectionDetails;
import com.synopsys.integration.detect.configuration.help.yaml.HelpYamlWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.property.types.path.SimplePathResolver;
import com.synopsys.integration.configuration.source.MapPropertySource;
import com.synopsys.integration.configuration.source.PropertySource;
import com.synopsys.integration.detect.configuration.DetectConfigurationFactory;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectPropertyConfiguration;
import com.synopsys.integration.detect.configuration.DetectPropertyUtil;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.DetectableOptionFactory;
import com.synopsys.integration.detect.configuration.enumeration.BlackduckScanMode;
import com.synopsys.integration.detect.configuration.enumeration.DetectGroup;
import com.synopsys.integration.detect.configuration.enumeration.DetectTargetType;
import com.synopsys.integration.detect.configuration.enumeration.DetectTool;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.configuration.help.DetectArgumentState;
import com.synopsys.integration.detect.configuration.help.json.HelpJsonManager;
import com.synopsys.integration.detect.configuration.help.print.HelpPrinter;
import com.synopsys.integration.detect.configuration.validation.DeprecationResult;
import com.synopsys.integration.detect.configuration.validation.DetectConfigurationBootManager;
import com.synopsys.integration.detect.interactive.InteractiveManager;
import com.synopsys.integration.detect.lifecycle.autonomous.AutonomousManager;
import com.synopsys.integration.detect.lifecycle.boot.decision.BlackDuckDecision;
import com.synopsys.integration.detect.lifecycle.boot.decision.ProductDecider;
import com.synopsys.integration.detect.lifecycle.boot.decision.RunDecision;
import com.synopsys.integration.detect.lifecycle.boot.product.ProductBoot;
import com.synopsys.integration.detect.lifecycle.run.data.ProductRunData;
import com.synopsys.integration.detect.lifecycle.run.singleton.BootSingletons;
import com.synopsys.integration.detect.tool.cache.InstalledToolLocator;
import com.synopsys.integration.detect.tool.cache.InstalledToolManager;
import com.synopsys.integration.detect.util.filter.DetectToolFilter;
import com.synopsys.integration.detect.workflow.airgap.AirGapCreator;
import com.synopsys.integration.detect.workflow.airgap.AirGapType;
import com.synopsys.integration.detect.workflow.airgap.AirGapTypeDecider;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticDecision;
import com.synopsys.integration.detect.workflow.diagnostic.DiagnosticSystem;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.rest.proxy.ProxyInfo;

import freemarker.template.Configuration;
import java.util.Set;

public class DetectBoot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final PrintStream DEFAULT_PRINT_STREAM = System.out;

    private final EventSystem eventSystem;
    private final Gson gson;
    private final DetectBootFactory detectBootFactory;
    private final DetectArgumentState detectArgumentState;
    private final List<PropertySource> propertySources;
    private final InstalledToolManager installedToolManager;
    private final List<DetectTool> rapidTools = Arrays.asList(DetectTool.DOCKER, DetectTool.DETECTOR);

    public DetectBoot(
        EventSystem eventSystem,
        Gson gson,
        DetectBootFactory detectBootFactory,
        DetectArgumentState detectArgumentState,
        List<PropertySource> propertySources,
        InstalledToolManager installedToolManager
    ) {
        this.eventSystem = eventSystem;
        this.gson = gson;
        this.detectBootFactory = detectBootFactory;
        this.detectArgumentState = detectArgumentState;
        this.propertySources = propertySources;
        this.installedToolManager = installedToolManager;
    }

    public Optional<DetectBootResult> boot(String detectVersion, String detectBuildDate) throws IOException, IllegalAccessException, NoSuchAlgorithmException {

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            HelpPrinter helpPrinter = new HelpPrinter();
            helpPrinter.printAppropriateHelpMessage(
                DEFAULT_PRINT_STREAM,
                DetectProperties.allProperties().getProperties(),
                Arrays.asList(DetectGroup.values()),
                DetectGroup.BLACKDUCK_SERVER,
                detectArgumentState
            );
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources, Collections.emptySortedMap())));
        }

        if (detectArgumentState.isHelpJsonDocument()) {
            HelpJsonManager helpJsonManager = detectBootFactory.createHelpJsonManager();
            helpJsonManager.createHelpJsonDocument(String.format("synopsys-detect-%s-help.json", detectVersion));
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources, Collections.emptySortedMap())));
        }

        if (detectArgumentState.isHelpYamlDocument()) {
            HelpYamlWriter helpYamlWriter = new HelpYamlWriter();
            helpYamlWriter.createHelpYamlDocument(String.format("synopsys-detect-%s-template-application.yml", detectVersion));
            return Optional.of(DetectBootResult.exit(new PropertyConfiguration(propertySources, Collections.emptySortedMap())));
        }

        DEFAULT_PRINT_STREAM.println();
        DEFAULT_PRINT_STREAM.println("Detect Version: " + detectVersion);
        DEFAULT_PRINT_STREAM.println();

        if (detectArgumentState.isInteractive()) {
            InteractiveManager interactiveManager = detectBootFactory.createInteractiveManager(propertySources);
            MapPropertySource interactivePropertySource = interactiveManager.executeInteractiveMode();
            propertySources.add(0, interactivePropertySource);
        }

        SortedMap<String, String> scanSettingsProperties = new TreeMap<>();

        PropertyConfiguration propertyConfiguration = new PropertyConfiguration(propertySources, scanSettingsProperties);

        SortedMap<String, String> maskedRawPropertyValues = collectMaskedRawPropertyValues(propertyConfiguration);

        publishCollectedPropertyValues(maskedRawPropertyValues);

        logger.debug("Configuration processed completely.");

        DetectConfigurationBootManager detectConfigurationBootManager = detectBootFactory.createDetectConfigurationBootManager(propertyConfiguration);
        DeprecationResult deprecationResult = detectConfigurationBootManager.createDeprecationNotesAndPublishEvents(propertyConfiguration);
        detectConfigurationBootManager.printConfiguration(maskedRawPropertyValues, deprecationResult.getAdditionalNotes());

        Optional<DetectUserFriendlyException> possiblePropertyParseError = detectConfigurationBootManager.validateForPropertyParseErrors();
        if (possiblePropertyParseError.isPresent()) {
            return Optional.of(DetectBootResult.exception(possiblePropertyParseError.get(), propertyConfiguration));
        }

        logger.info("Detect build date: {}", detectBuildDate);
        logger.debug("Initializing Detect.");

        Configuration freemarkerConfiguration = detectBootFactory.createFreemarkerConfiguration();
        DetectPropertyConfiguration detectConfiguration = new DetectPropertyConfiguration(propertyConfiguration, new SimplePathResolver());

        DetectConfigurationFactory detectConfigurationFactory = new DetectConfigurationFactory(detectConfiguration, gson);

        boolean autonomousScanEnabled = detectConfiguration.getValue(DetectProperties.DETECT_AUTONOMOUS_SCAN_ENABLED);

        DirectoryManager directoryManager = detectBootFactory.createDirectoryManager(detectConfigurationFactory);

        // TODO Scan settings model obtained below is to be used by the delta-checking operations
        AutonomousManager autonomousManager = new AutonomousManager(directoryManager, detectConfiguration, autonomousScanEnabled, maskedRawPropertyValues);

        if(autonomousScanEnabled) {
            scanSettingsProperties = autonomousManager.getAllScanSettingsProperties();
            propertyConfiguration.setScanSettingsProperties(scanSettingsProperties);
        }

        InstalledToolLocator installedToolLocator = new InstalledToolLocator(directoryManager.getPermanentDirectory().toPath(), gson);

        DiagnosticSystem diagnosticSystem = null;
        DiagnosticDecision diagnosticDecision = DiagnosticDecision.decide(detectArgumentState, propertyConfiguration);
        if (diagnosticDecision.shouldCreateDiagnosticSystem()) {
            diagnosticSystem = detectBootFactory.createDiagnosticSystem(
                propertyConfiguration,
                directoryManager,
                maskedRawPropertyValues
            );
        }
        logger.debug("Main boot completed. Deciding what Detect should do.");

        if (detectArgumentState.isGenerateAirGapZip()) {
            try {
                AirGapType airGapType = new AirGapTypeDecider().decide(detectArgumentState);
                AirGapCreator airGapCreator = detectBootFactory
                    .createAirGapCreator(
                        detectConfigurationFactory.createConnectionDetails(),
                        detectConfigurationFactory.createDetectExecutableOptions(),
                        freemarkerConfiguration,
                        installedToolManager,
                        installedToolLocator
                    );

                File airGapZip = airGapCreator.createAirGapZip(airGapType, directoryManager.getRunHomeDirectory());

                return Optional.of(DetectBootResult.exit(propertyConfiguration, airGapZip, directoryManager, diagnosticSystem));
            } catch (DetectUserFriendlyException e) {
                return Optional.of(DetectBootResult.exception(e, propertyConfiguration, directoryManager, diagnosticSystem));
            }
        }

        logger.info("");

        ProductRunData productRunData;

        // store the result of hasImageOrTar... we will need this in more than one place.
        boolean hasImageOrTar;
        DetectableOptionFactory detectableOptionFactory;
        try {
            ProxyInfo detectableProxyInfo = detectConfigurationFactory.createBlackDuckProxyInfo();
            detectableOptionFactory = new DetectableOptionFactory(detectConfiguration, diagnosticSystem, detectableProxyInfo);
            hasImageOrTar = detectableOptionFactory.createDockerDetectableOptions().hasDockerImageOrTar();
            oneRequiresTheOther(
                detectConfigurationFactory.createDetectTarget() == DetectTargetType.IMAGE,
                hasImageOrTar,
                "Detect target type is set to IMAGE, but no docker image was specified."
            );
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, propertyConfiguration, directoryManager, diagnosticSystem));
        }
        
        Map<DetectTool, Set<String>> scanTypeEvidenceMap = autonomousManager.getScanTypeMap(hasImageOrTar);

        try {
            boolean blackduckScanModeSpecified = detectConfiguration.wasPropertyProvided(DetectProperties.DETECT_BLACKDUCK_SCAN_MODE);
            BlackDuckConnectionDetails blackDuckConnectionDetails = detectConfigurationFactory.createBlackDuckConnectionDetails();
            BlackduckScanMode blackduckScanMode = decideScanMode(blackDuckConnectionDetails, scanTypeEvidenceMap, blackduckScanModeSpecified, detectConfigurationFactory, autonomousScanEnabled, detectConfiguration);
            autonomousManager.setBlackDuckScanMode(blackduckScanMode.toString());
            ProductDecider productDecider = new ProductDecider();
            BlackDuckDecision blackDuckDecision = productDecider.decideBlackDuck(
                blackDuckConnectionDetails,
                blackduckScanMode,
                detectConfigurationFactory.createHasSignatureScan(scanTypeEvidenceMap.containsKey(DetectTool.SIGNATURE_SCAN))
            );

            // in order to know if docker is needed we have to have either detect.target.type=IMAGE or detect.docker.image
            RunDecision runDecision = new RunDecision(detectConfigurationFactory.createDetectTarget() == DetectTargetType.IMAGE || hasImageOrTar, detectConfigurationFactory.createDetectTarget()); //TODO: Move to proper decision home. -jp
            DetectToolFilter detectToolFilter = detectConfigurationFactory.createToolFilter(runDecision, blackDuckDecision, scanTypeEvidenceMap);
            oneRequiresTheOther(
                detectConfigurationFactory.createDetectTarget() == DetectTargetType.IMAGE,
                detectToolFilter.shouldInclude(DetectTool.DOCKER),
                "Detect target type is set to IMAGE, but the DOCKER tool was excluded."
            );

            logger.debug("Decided what products will be run. Starting product boot.");

            ProductBoot productBoot = detectBootFactory.createProductBoot(detectConfigurationFactory, detectToolFilter, blackduckScanMode);
            productRunData = productBoot.boot(blackDuckDecision, detectToolFilter);
        } catch (DetectUserFriendlyException e) {
            return Optional.of(DetectBootResult.exception(e, propertyConfiguration, directoryManager, diagnosticSystem));
        }

        if (productRunData == null) {
            logger.info("No products to run, Detect is complete.");
            return Optional.of(DetectBootResult.exit(propertyConfiguration, directoryManager, diagnosticSystem));
        }

        BootSingletons bootSingletons = detectBootFactory
            .createRunDependencies(
                productRunData,
                propertyConfiguration,
                detectableOptionFactory,
                detectConfigurationFactory,
                directoryManager,
                freemarkerConfiguration,
                installedToolManager,
                installedToolLocator,
                autonomousManager
            );

        return Optional.of(DetectBootResult.run(bootSingletons, propertyConfiguration, productRunData, directoryManager, diagnosticSystem));
    }

    private BlackduckScanMode decideScanMode(BlackDuckConnectionDetails blackDuckConnectionDetails, Map<DetectTool, Set<String>> scanTypeEvidenceMap, boolean blackduckScanModeSpecified, DetectConfigurationFactory detectConfigurationFactory, boolean autonomousScanEnabled, DetectPropertyConfiguration detectConfiguration) {
        if(!blackduckScanModeSpecified && autonomousScanEnabled) {
            Optional<String> scaasFilePath = detectConfigurationFactory.getScaaasFilePath();
            Optional<String> blackDuckUrl = blackDuckConnectionDetails.getBlackDuckUrl();

            AllEnumList<DetectTool> detectTools = detectConfiguration.getValue(DetectProperties.DETECT_TOOLS);

            if (blackDuckUrl.isPresent()) {
                boolean isNotRapid = detectTools.representedValues().stream().anyMatch(tool -> !rapidTools.contains(tool)) || scanTypeEvidenceMap.keySet().stream().anyMatch(tool -> !rapidTools.contains(tool));
                if (!scanTypeEvidenceMap.isEmpty() && !isNotRapid && scaasFilePath.isPresent()) {
                    return BlackduckScanMode.RAPID;
                } else if (!scanTypeEvidenceMap.isEmpty() && scaasFilePath.isPresent()) {
                    return BlackduckScanMode.STATELESS;
                }
            }
            return BlackduckScanMode.INTELLIGENT;
        }
        return detectConfigurationFactory.createScanMode();
    }

    private void oneRequiresTheOther(boolean firstCondition, boolean secondCondition, String errorMessageIfNot) throws DetectUserFriendlyException {
        if (firstCondition && !secondCondition) {
            throw new DetectUserFriendlyException(
                "Invalid configuration: " + errorMessageIfNot,
                ExitCodeType.FAILURE_CONFIGURATION
            );
        }
    }

    private SortedMap<String, String> collectMaskedRawPropertyValues(PropertyConfiguration propertyConfiguration) throws IllegalAccessException {
        return new TreeMap<>(propertyConfiguration.getMaskedRawValueMap(
            new HashSet<>(DetectProperties.allProperties().getProperties()),
            DetectPropertyUtil.getPasswordsAndTokensPredicate()
        ));
    }

    private void publishCollectedPropertyValues(Map<String, String> maskedRawPropertyValues) {
        eventSystem.publishEvent(Event.RawMaskedPropertyValuesCollected, new TreeMap<>(maskedRawPropertyValues));
    }
}
