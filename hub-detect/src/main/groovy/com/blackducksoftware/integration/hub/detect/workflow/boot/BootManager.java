package com.blackducksoftware.integration.hub.detect.workflow.boot;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.env.ConfigurableEnvironment;

import com.blackducksoftware.integration.hub.detect.Application;
import com.blackducksoftware.integration.hub.detect.BeanConfiguration;
import com.blackducksoftware.integration.hub.detect.DetectInfo;
import com.blackducksoftware.integration.hub.detect.configuration.ConfigurationManager;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigurationUtility;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.DetectPropertySource;
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
import com.blackducksoftware.integration.hub.detect.interactive.mode.InteractiveMode;
import com.blackducksoftware.integration.hub.detect.property.PropertyMap;
import com.blackducksoftware.integration.hub.detect.util.TildeInPathResolver;
import com.blackducksoftware.integration.hub.detect.workflow.PhoneHomeManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DetectRunManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticFileManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticLogManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticManager;
import com.blackducksoftware.integration.hub.detect.workflow.diagnostic.DiagnosticReportManager;
import com.blackducksoftware.integration.hub.detect.workflow.profiling.BomToolProfiler;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.synopsys.integration.blackduck.service.HubServicesFactory;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentLogger;

import freemarker.template.Configuration;

public class BootManager {

    public BootResult boot(final String[] sourceArgs, ConfigurableEnvironment environment) throws DetectUserFriendlyException, IntegrationException {

        Gson gson = HubServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create();
        JsonParser jsonParser = new JsonParser();

        DetectInfo detectInfo = new DetectInfo();
        detectInfo.init();

        DetectRunManager detectRunManager = new DetectRunManager();
        detectRunManager.init();

        DetectPropertySource detectPropertySource = new DetectPropertySource(environment);
        detectPropertySource.init();
        PropertyMap<DetectProperty> detectPropertyMap = new PropertyMap<>();
        DetectConfiguration detectConfiguration = new DetectConfiguration(detectPropertySource, detectPropertyMap);
        detectConfiguration.init();

        DetectOptionManager detectOptionManager = new DetectOptionManager(detectConfiguration, detectInfo);
        detectOptionManager.init();
        final List<DetectOption> options = detectOptionManager.getDetectOptions();

        DetectArgumentStateParser detectArgumentStateParser = new DetectArgumentStateParser();
        final DetectArgumentState detectArgumentState = detectArgumentStateParser.parseArgs(sourceArgs);

        DetectConfigurationUtility detectConfigurationUtility = new DetectConfigurationUtility(detectConfiguration);
        HubServiceManager hubServiceManager = new HubServiceManager(detectConfiguration, detectConfigurationUtility, gson, jsonParser);

        InteractiveMode defaultInteractiveMode = new DefaultInteractiveMode(hubServiceManager, detectOptionManager);
        InteractiveManager interactiveManager = new InteractiveManager(detectOptionManager, defaultInteractiveMode);

        if (detectArgumentState.isHelp() || detectArgumentState.isDeprecatedHelp() || detectArgumentState.isVerboseHelp()) {
            HelpPrinter helpPrinter = new HelpPrinter();
            helpPrinter.printAppropriateHelpMessage(System.out, options, detectArgumentState);
            return BootResult.exit();
        }

        if (detectArgumentState.isHelpDocument()) {
            HelpHtmlWriter helpHtmlWriter = new HelpHtmlWriter(detectOptionManager, createConfiguration());
            helpHtmlWriter.writeHelpMessage(String.format("hub-detect-%s-help.html", detectInfo.getDetectVersion()));
            return BootResult.exit();
        }

        DetectInfoPrinter detectInfoPrinter = new DetectInfoPrinter();
        detectInfoPrinter.printInfo(System.out, detectInfo);

        if (detectArgumentState.isInteractive()) {
            interactiveManager.configureInInteractiveMode();
        }

        TildeInPathResolver tildeInPathResolver = new TildeInPathResolver(ConfigurationManager.USER_HOME, detectInfo.getCurrentOs());
        ConfigurationManager configurationManager = new ConfigurationManager(tildeInPathResolver, detectConfiguration);
        final List<String> defaultBdioLocation = new ArrayList<>();
        defaultBdioLocation.add("bdio");
        if (detectArgumentState.isDiagnostic()) {
            defaultBdioLocation.add(detectRunManager.getRunId());
        }
        configurationManager.initialize(options, defaultBdioLocation);
        detectOptionManager.postInit();

        //logger.info("Configuration processed completely.");

        BomToolProfiler bomToolProfiler = new BomToolProfiler();
        DiagnosticReportManager diagnosticReportManager = new DiagnosticReportManager(bomToolProfiler);
        DiagnosticLogManager diagnosticLogManager = new DiagnosticLogManager();
        DiagnosticFileManager diagnosticFileManager = new DiagnosticFileManager();
        DiagnosticManager diagnosticManager = new DiagnosticManager(detectConfiguration, diagnosticReportManager, diagnosticLogManager, detectRunManager, diagnosticFileManager);
        diagnosticManager.init(detectArgumentState.isDiagnostic(), detectArgumentState.isDiagnosticProtected());

        DetectConfigurationPrinter detectConfigurationPrinter = new DetectConfigurationPrinter();
        if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_SUPPRESS_CONFIGURATION_OUTPUT)){
            detectConfigurationPrinter.print(System.out, options);
        }
        detectConfigurationPrinter.printWarnings(System.out, options);

        final List<DetectOption.OptionValidationResult> invalidDetectOptionResults = detectOptionManager.getAllInvalidOptionResults();
        if (!invalidDetectOptionResults.isEmpty()) {
            throw new DetectUserFriendlyException(invalidDetectOptionResults.get(0).getValidationMessage(), ExitCodeType.FAILURE_GENERAL_ERROR);
        }

        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_TEST_CONNECTION)) {
            hubServiceManager.assertHubConnection(new SilentLogger());
            return BootResult.exit();
        }

        if (detectConfiguration.getBooleanProperty(DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK) && !hubServiceManager.testHubConnection(new SilentLogger())) {
            //logger.info(String.format("%s is set to 'true' so Detect will not run.", DetectProperty.DETECT_DISABLE_WITHOUT_BLACKDUCK.getPropertyName()));
            return BootResult.exit();
        }

        PhoneHomeManager phoneHomeManager = new PhoneHomeManager(detectInfo, detectConfiguration, gson);
        if (detectConfiguration.getBooleanProperty(DetectProperty.BLACKDUCK_OFFLINE_MODE)) {
            phoneHomeManager.initOffline();
        } else {
            hubServiceManager.init();
            phoneHomeManager.init(hubServiceManager.createPhoneHomeService(), hubServiceManager.createPhoneHomeClient(), hubServiceManager.getHubServicesFactory(), hubServiceManager.createHubService(),
                hubServiceManager.createHubRegistrationService(), hubServiceManager.getHubServicesFactory().getRestConnection().getBaseUrl());

            phoneHomeManager.startPhoneHome();
        }

        BootResult result = new BootResult();
        DetectContext detectContext = new DetectContext();
        detectContext.detectConfiguration = detectConfiguration;
        return result;
    }

    private  Configuration  createConfiguration(){
        final Configuration configuration = new Configuration(Configuration.VERSION_2_3_26);
        configuration.setClassForTemplateLoading(BeanConfiguration.class, "/");
        configuration.setDefaultEncoding("UTF-8");
        configuration.setLogTemplateExceptions(true);
        return configuration;
    }
}
