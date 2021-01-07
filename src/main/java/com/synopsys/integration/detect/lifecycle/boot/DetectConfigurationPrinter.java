package com.synopsys.integration.detect.lifecycle.boot;

import java.util.ArrayList;
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
import com.synopsys.integration.detect.lifecycle.shutdown.ExitCodeRequest;
import com.synopsys.integration.detect.workflow.event.Event;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.report.writer.InfoLogReportWriter;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;

public class DetectConfigurationPrinter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventSystem eventSystem;
    private final DetectInfo detectInfo;

    public DetectConfigurationPrinter(EventSystem eventSystem, DetectInfo detectInfo) {
        this.eventSystem = eventSystem;
        this.detectInfo = detectInfo;
    }

    // TODO: This method seems to violate SRP -- not only are we printing the configuration, but also validating it for failure conditions. --rotte JAN 2021
    public Optional<DetectBootResult> printConfiguration(PropertyConfiguration detectConfiguration) throws IllegalAccessException {
        Boolean printFull = detectConfiguration.getValueOrDefault(DetectProperties.DETECT_SUPPRESS_CONFIGURATION_OUTPUT.getProperty());

        Map<String, String> additionalNotes = new HashMap<>();

        List<Property> deprecatedProperties = DetectProperties.allProperties()
                                                  .stream()
                                                  .filter(property -> property.getPropertyDeprecationInfo() != null)
                                                  .collect(Collectors.toList());

        Map<String, List<String>> deprecationMessages = new HashMap<>();
        List<Property> usedFailureProperties = new ArrayList<>();
        for (Property property : deprecatedProperties) {
            if (detectConfiguration.wasKeyProvided(property.getKey())) {
                PropertyDeprecationInfo deprecationInfo = property.getPropertyDeprecationInfo();

                if (deprecationInfo == null) {
                    logger.debug("A deprecated property is missing deprecation info.");
                    continue;
                }

                additionalNotes.put(property.getKey(), "\t *** DEPRECATED ***");
                String deprecationMessage = deprecationInfo.getDeprecationText();

                deprecationMessages.put(property.getKey(), new ArrayList<>(Collections.singleton(deprecationMessage)));
                DetectIssue.publish(eventSystem, DetectIssueType.DEPRECATION, property.getKey(), "\t" + deprecationMessage);

                if (detectInfo.getDetectMajorVersion() >= deprecationInfo.getFailInVersion().getIntValue()) {
                    usedFailureProperties.add(property);
                }
            }
        }

        //First print the entire configuration.
        PropertyConfigurationHelpContext detectConfigurationReporter = new PropertyConfigurationHelpContext(detectConfiguration);
        InfoLogReportWriter infoLogReportWriter = new InfoLogReportWriter();
        if (!printFull) {
            detectConfigurationReporter.printCurrentValues(infoLogReportWriter::writeLine, DetectProperties.allProperties(), additionalNotes);
        }

        //Next check for options that are just plain bad, ie giving an detector type we don't know about.
        Map<String, List<String>> errorMap = detectConfigurationReporter.findPropertyParseErrors(DetectProperties.allProperties());
        if (errorMap.size() > 0) {
            Map.Entry<String, List<String>> entry = errorMap.entrySet().iterator().next();
            return Optional.of(DetectBootResult.exception(new DetectUserFriendlyException(entry.getKey() + ": " + entry.getValue().get(0), ExitCodeType.FAILURE_GENERAL_ERROR), detectConfiguration));
        }

        if (usedFailureProperties.isEmpty()) {
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
