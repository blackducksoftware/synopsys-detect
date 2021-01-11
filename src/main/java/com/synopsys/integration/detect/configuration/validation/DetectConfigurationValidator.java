package com.synopsys.integration.detect.configuration.validation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.configuration.property.PropertyDeprecationInfo;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.lifecycle.boot.DetectBootResult;
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;

public class DetectConfigurationValidator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventSystem eventSystem;
    private final DetectInfo detectInfo;
    private final InfoLogReportWriter infoLogReportWriter;

    public DetectConfigurationValidator(EventSystem eventSystem, DetectInfo detectInfo) {
        this.eventSystem = eventSystem;
        this.detectInfo = detectInfo;
        this.infoLogReportWriter = new InfoLogReportWriter();
    }

    public DetectConfigurationState processDetectConfiguration(PropertyConfiguration detectConfiguration) throws IllegalAccessException {
        Map<String, String> additionalNotes = new HashMap<>();
        Map<String, List<String>> deprecationMessages = new HashMap<>();
        boolean hasUsedFailureProperties = false;

        List<Property> usedDeprecatedProperties = DetectProperties.allProperties()
                                                      .stream()
                                                      .filter(property -> property.getPropertyDeprecationInfo() != null)
                                                      .filter(property -> detectConfiguration.wasKeyProvided(property.getKey()))
                                                      .collect(Collectors.toList());

        for (Property property : usedDeprecatedProperties) {
            PropertyDeprecationInfo deprecationInfo = property.getPropertyDeprecationInfo();
            String deprecationMessage = deprecationInfo.getDeprecationText();
            String propertyKey = property.getKey();

            additionalNotes.put(propertyKey, "\t *** DEPRECATED ***");

            deprecationMessages.put(propertyKey, Collections.singletonList(deprecationMessage));
            DetectIssue.publish(eventSystem, DetectIssueType.DEPRECATION, propertyKey, "\t" + deprecationMessage);

            if (!hasUsedFailureProperties && detectInfo.getDetectMajorVersion() >= deprecationInfo.getFailInVersion().getIntValue()) {
                hasUsedFailureProperties = true;
            }
        }

        return new DetectConfigurationState(detectConfiguration, additionalNotes, deprecationMessages, hasUsedFailureProperties);
    }

    public void printConfiguration(PropertyConfigurationHelpContext detectConfigurationReporter, PropertyConfiguration detectConfiguration, Map<String, String> additionalNotes) throws IllegalAccessException {
        Boolean suppressConfigurationOutput = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_SUPPRESS_CONFIGURATION_OUTPUT.getProperty());

        //First print the entire configuration.
        if (Boolean.FALSE.equals(suppressConfigurationOutput)) {
            detectConfigurationReporter.printCurrentValues(infoLogReportWriter::writeLine, DetectProperties.allProperties(), additionalNotes);
        }
    }


    public Optional<DetectBootResult> validateConfiguration(PropertyConfigurationHelpContext detectConfigurationReporter, PropertyConfiguration detectConfiguration, Map<String, List<String>> deprecationMessages, boolean noFailurePropertiesUsed) throws IllegalAccessException {
        //Check for options that are just plain bad, ie giving an detector type we don't know about.
        Map<String, List<String>> errorMap = detectConfigurationReporter.findPropertyParseErrors(DetectProperties.allProperties());
        if (errorMap.size() > 0) {
            Map.Entry<String, List<String>> entry = errorMap.entrySet().iterator().next();
            return Optional.of(DetectBootResult.exception(new DetectUserFriendlyException(entry.getKey() + ": " + entry.getValue().get(0), ExitCodeType.FAILURE_GENERAL_ERROR), detectConfiguration));
        }

        if (noFailurePropertiesUsed) {
            detectConfigurationReporter.printPropertyErrors(infoLogReportWriter::writeLine, DetectProperties.allProperties(), deprecationMessages);

            logger.warn(StringUtils.repeat("=", 60));
            logger.warn("Configuration is using deprecated properties that must be updated for this major version.");
            logger.warn("You MUST fix these deprecation issues for detect to proceed.");
            logger.warn(String.format("To ignore these messages and force detect to exit with success supply --%s=true", DetectProperties.DETECT_FORCE_SUCCESS.getProperty().getKey()));
            logger.warn("This will not force detect to run, but it will pretend to have succeeded.");
            logger.warn(StringUtils.repeat("=", 60));

            eventSystem.publishEvent(Event.ExitCode, new ExitCodeRequest(ExitCodeType.FAILURE_CONFIGURATION));
            return Optional.of(DetectBootResult.exit(detectConfiguration));
        }

        return Optional.empty();
    }

}
