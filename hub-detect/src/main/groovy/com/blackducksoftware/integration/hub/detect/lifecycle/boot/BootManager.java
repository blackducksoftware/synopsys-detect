package com.blackducksoftware.integration.hub.detect.lifecycle.boot;

import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.DetectInfoUtility;
import com.blackducksoftware.integration.hub.detect.DetectorBeanConfiguration;
import com.blackducksoftware.integration.hub.detect.RunBeanConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertyMap;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.blackducksoftware.integration.hub.detect.exitcode.ExitCodeType;
import com.blackducksoftware.integration.hub.detect.help.DetectArgumentState;
import com.blackducksoftware.integration.hub.detect.help.DetectArgumentStateParser;
import com.blackducksoftware.integration.hub.detect.help.DetectOption;
import com.blackducksoftware.integration.hub.detect.help.DetectOptionManager;
import com.blackducksoftware.integration.hub.detect.help.html.HelpHtmlWriter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectConfigurationPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.DetectInfoPrinter;
import com.blackducksoftware.integration.hub.detect.help.print.HelpPrinter;
import com.blackducksoftware.integration.hub.detect.hub.HubServiceManager;
import com.blackducksoftware.integration.hub.detect.interactive.InteractiveManager;
import com.blackducksoftware.integration.hub.detect.interactive.mode.DefaultInteractiveMode;
import com.blackducksoftware.integration.hub.detect.lifecycle.DetectContext;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.blackducksoftware.integration.hub.detect.workflow.DetectConfigurationFactory;
import com.blackducksoftware.integration.hub.detect.workflow.DetectRun;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.FileManager;
import com.blackducksoftware.integration.hub.detect.workflow.event.EventSystem;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.OfflinePhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.OnlinePhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.phonehome.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.BomToolProfiler;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentLogger;

import freemarker.template.Configuration;

public class BootManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BootFactory bootFactory;

    public BootManager(BootFactory bootFactory) {
        this.bootFactory = bootFactory;
    }

    public BootResult boot(DetectRun detectRun, final String[] sourceArgs, ConfigurableEnvironment environment, EventSystem eventSystem, DetectContext detectContext) throws DetectUserFriendlyException, IntegrationException {
        Gson gson = bootFactory.createGson();
        JsonParser jsonParser = bootFactory.createJsonParser();
        DocumentBuilder xml = bootFactory.createXmlDocumentBuilder();
        Configuration configuration = bootFactory.createConfiguration();

        DetectInfo detectInfo = DetectInfoUtility.createDefaultDetectInfo();

        DetectPropertySource propertySource = new DetectPropertySource(environment);
        DetectPropertyMap propertyMap = new DetectPropertyMap();
        DetectConfiguration detectConfiguration = new DetectConfiguration(propertySource, propertyMap);
        DetectOptionManager detectOptionManager = new DetectOptionManager(detectConfiguration, detectInfo);

        final List<DetectOption> options = detectOptionManager.getDetectOptions();

        DetectArgumentState detectArgumentState = parseDetectArgumentState(sourceArgs);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            printAppropriateHelp(options, detectArgumentState);
            return BootResult.exit();
        }

        if (detectArgumentState.isHelpDocument()) {
            printHelpDocument(options, detectInfo, configuration);
            return BootResult.exit();
        }

        printDetectInfo(detectInfo);

        if (detectArgumentState.isInteractive()) {
            startInteractiveMode(detectOptionManager, detectConfiguration, gson, jsonParser);
        }

        processDetectConfiguration(detectInfo, detectRun, detectConfiguration, options);

        detectOptionManager.postConfigurationProcessedInit();

        logger.info("Configuration processed completely.");

        DetectConfigurationFactory factory = new DetectConfigurationFactory(detectConfiguration);
        DirectoryManager directoryManager = new DirectoryManager(factory.createDirectoryOptions(), detectRun);
        FileManager fileManager = new FileManager(detectArgumentState.isDiagnostic(),
            detectArgumentState.isDiagnosticProtected(), directoryManager);

        DiagnosticManager diagnosticManager = createDiagnostics(detectConfiguration, detectRun, detectArgumentState, eventSystem, directoryManager, fileManager);

        printConfiguration(detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_CONFIGURATION_OUTPUT, PropertyAuthority.None), options);

        checkForInvalidOptions(detectOptionManager);

        HubServiceManager hubServiceManager = new HubServiceManager(detectConfiguration, new ConnectionManager(detectConfiguration), gson, jsonParser);

        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION, PropertyAuthority.None)) {
            hubServiceManager.assertHubConnection(new SilentLogger());
            return BootResult.exit();
        }

        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK, PropertyAuthority.None) && !hubServiceManager.testHubConnection(new SilentLogger())) {
            logger.info(String.format("%s is set to 'true' so Detect will not run.", DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK.getPropertyName()));
            return BootResult.exit();
        }

        if (!detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None)) {
            hubServiceManager.init();
        }

        PhoneHomeManager phoneHomeManager = createPhoneHomeManager(detectInfo, detectConfiguration, hubServiceManager, eventSystem, gson);

        //lock the configuration, boot has completed.
        logger.debug("Configuration is now complete. No changes should occur to configuration.");
        detectConfiguration.lock();

        //Finished, populate the detect context
        detectContext.registerBean(detectRun);
        detectContext.registerBean(eventSystem);

        detectContext.registerBean(detectConfiguration);
        detectContext.registerBean(detectInfo);
        detectContext.registerBean(directoryManager);
        detectContext.registerBean(phoneHomeManager);
        detectContext.registerBean(diagnosticManager);
        detectContext.registerBean(hubServiceManager);

        detectContext.registerBean(gson);
        detectContext.registerBean(jsonParser);
        detectContext.registerBean(xml);
        detectContext.registerBean(configuration);

        detectContext.registerConfiguration(RunBeanConfiguration.class);
        detectContext.registerConfiguration(DetectorBeanConfiguration.class);
        detectContext.lock(); //can only refresh once, this locks and triggers refresh.

        BootResult result = new BootResult();
        result.bootType = BootResult.BootType.CONTINUE;
        result.detectConfiguration = detectConfiguration;
        return result;
    }

    private void printAppropriateHelp(List<DetectOption> detectOptions, DetectArgumentState detectArgumentState) {
        HelpPrinter helpPrinter = new HelpPrinter();
        helpPrinter.printAppropriateHelpMessage(System.out, detectOptions, detectArgumentState);
    }

    private void printHelpDocument(List<DetectOption> detectOptions, DetectInfo detectInfo, Configuration configuration) {
        HelpHtmlWriter helpHtmlWriter = new HelpHtmlWriter(configuration);
        helpHtmlWriter.writeHtmlDocument(String.format("hub-detect-%s-help.html", detectInfo.getDetectVersion()), detectOptions);
    }

    private void printDetectInfo(DetectInfo detectInfo) {
        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);
    }

    private void printConfiguration(boolean fullConfiguration, List<DetectOption> detectOptions) {
        DetectConfigurationPrinter detectConfigurationPrinter = new DetectConfigurationPrinter();
        if (!fullConfiguration) {
            detectConfigurationPrinter.print(System.out, detectOptions);
        }
        detectConfigurationPrinter.printWarnings(System.out, detectOptions);
    }

    private void startInteractiveMode(DetectOptionManager detectOptionManager, DetectConfiguration detectConfiguration, Gson gson, JsonParser jsonParser) {
        InteractiveManager interactiveManager = new InteractiveManager(detectOptionManager);
        HubServiceManager hubServiceManager = new HubServiceManager(detectConfiguration, new ConnectionManager(detectConfiguration), gson, jsonParser);
        DefaultInteractiveMode defaultInteractiveMode = new DefaultInteractiveMode(hubServiceManager, detectOptionManager);
        interactiveManager.configureInInteractiveMode(defaultInteractiveMode);
    }

    private DetectArgumentState parseDetectArgumentState(String[] sourceArgs) {
        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        final DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);
        return detectArgumentState;
    }

    private void processDetectConfiguration(DetectInfo detectInfo, DetectRun detectRun, DetectConfiguration detectConfiguration, List<DetectOption> detectOptions) throws DetectUserFriendlyException {
        TildeInPathResolver tildeInPathResolver = new TildeInPathResolver(DetectConfigurationManager.USER_HOME, detectInfo.getCurrentOs());
        DetectConfigurationManager detectConfigurationManager = new DetectConfigurationManager(tildeInPathResolver, detectConfiguration);
        detectConfigurationManager.process(detectOptions, detectRun.getRunId());
    }

    private void checkForInvalidOptions(DetectOptionManager detectOptionManager) throws DetectUserFriendlyException {
        final List<DetectOption.OptionValidationResult> invalidDetectOptionResults = detectOptionManager.getAllInvalidOptionResults();
        if (!invalidDetectOptionResults.isEmpty()) {
            throw new DetectUserFriendlyException(invalidDetectOptionResults.get(0).getValidationMessage(), ExitCodeType.FAILURE_GENERAL_ERROR);
        }
    }

    private DiagnosticManager createDiagnostics(DetectConfiguration detectConfiguration, DetectRun detectRun, DetectArgumentState detectArgumentState, EventSystem eventSystem, DirectoryManager directoryManager, FileManager fileManager) {
        BomToolProfiler profiler = new BomToolProfiler(eventSystem); //TODO: I think phone home needs one?
        DiagnosticManager diagnosticManager = new DiagnosticManager(detectConfiguration, detectRun, fileManager, detectArgumentState.isDiagnostic(),
            detectArgumentState.isDiagnosticProtected(), directoryManager, eventSystem, profiler);
        return diagnosticManager;
    }

    private PhoneHomeManager createPhoneHomeManager(DetectInfo detectInfo, DetectConfiguration detectConfiguration, HubServiceManager hubServiceManager, EventSystem eventSystem, Gson gson) {
        boolean online = !detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE, PropertyAuthority.None);

        Map<String, String> additionalMetaData = detectConfiguration.getPhoneHomeProperties();
        if (online) {
            return new OnlinePhoneHomeManager(additionalMetaData, detectInfo, gson, eventSystem, hubServiceManager);
        } else {
            return new OfflinePhoneHomeManager(additionalMetaData, detectInfo, gson, eventSystem);
        }
    }
}
