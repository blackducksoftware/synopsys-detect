package com.synopsys.integration.detect.configuration.validation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;
import com.synopsys.integration.configuration.property.base.TypedProperty;
import com.synopsys.integration.configuration.property.deprecation.DeprecatedValueUsage;
import com.synopsys.integration.detect.configuration.DetectProperties;
import com.synopsys.integration.detect.configuration.DetectUserFriendlyException;
import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.event.EventSystem;
import com.synopsys.integration.detect.workflow.status.DetectIssue;
import com.synopsys.integration.detect.workflow.status.DetectIssueType;

public class DetectConfigurationBootManager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final EventSystem eventSystem;
    private final PropertyConfigurationHelpContext detectConfigurationReporter;

    public DetectConfigurationBootManager(EventSystem eventSystem, PropertyConfigurationHelpContext detectConfigurationReporter) {
        this.eventSystem = eventSystem;
        this.detectConfigurationReporter = detectConfigurationReporter;
    }

    public List<RemovalDeprecation> checkForUsedRemovalDeprecations(PropertyConfiguration detectConfiguration) {
        return DetectProperties.allProperties().getProperties()
            .stream()
            .filter(property -> detectConfiguration.wasKeyProvided(property.getKey()))
            .filter(property -> property.getPropertyDeprecationInfo().getRemovalInfo().isPresent())
            .map(property -> new RemovalDeprecation(property.getKey(), property.getPropertyDeprecationInfo().getRemovalInfo().get().getDeprecationText()))
            .collect(Collectors.toList());
    }

    public List<ValueDeprecation> checkForUsedValueDeprecations(PropertyConfiguration detectConfiguration) {
        return DetectProperties.allTypedProperties()
            .stream()
            .filter(property -> detectConfiguration.wasKeyProvided(property.getKey()))
            .filter(property -> property.getPropertyDeprecationInfo().getDeprecatedValues().size() > 0)
            .map(property -> new ValueDeprecation(property.getKey(), checkForUsedValueDeprecationsForProperty(property, detectConfiguration)))
            .filter(valueDeprecation -> valueDeprecation.getDeprecatedValueUsages().size() > 0)
            .collect(Collectors.toList());
    }

    private <V, E> List<DeprecatedValueUsage> checkForUsedValueDeprecationsForProperty(TypedProperty<V, E> property, PropertyConfiguration detectConfiguration) {
        Optional<V> providedParsedValue = detectConfiguration.getProvidedParsedValue(property);
        return providedParsedValue.map(property::checkForDeprecatedValues)
            .orElseGet(Collections::emptyList);
    }

    public DeprecationResult createDeprecationNotesAndPublishEvents(PropertyConfiguration propertyConfiguration) {
        Map<String, String> additionalNotes = new HashMap<>();

        List<RemovalDeprecation> removedUsages = checkForUsedRemovalDeprecations(propertyConfiguration);
        List<ValueDeprecation> valueUsages = checkForUsedValueDeprecations(propertyConfiguration);

        Map<String, List<String>> issueMessages = new HashMap<>();
        for (RemovalDeprecation removal : removedUsages) {
            additionalNotes.put(removal.getPropertyKey(), "\t *** DEPRECATED ***");
            issueMessages.put(removal.getPropertyKey(), new ArrayList<>());
            issueMessages.get(removal.getPropertyKey()).add(removal.getDeprecationText());
        }

        for (ValueDeprecation value : valueUsages) {
            if (additionalNotes.containsKey(value.getPropertyKey()))
                continue;

            additionalNotes.put(value.getPropertyKey(), "\t *** DEPRECATED VALUE ***");
            value.getDeprecatedValueUsages().forEach(usage -> {
                issueMessages.computeIfAbsent(value.getPropertyKey(), (key) -> new ArrayList<>());
                issueMessages.get(value.getPropertyKey()).add(usage.getValue() + ": " + usage.getInfo().getReason());
            });
        }

        issueMessages.forEach((key, messages) -> {
            DetectIssue.publish(eventSystem, DetectIssueType.DEPRECATION, key, messages); //TODO: Publish new issue type specifically for deprecated values.
        });

        return new DeprecationResult(additionalNotes);
    }

    public void printConfiguration(SortedMap<String, String> maskedRawPropertyValues, Map<String, String> additionalNotes) {
        detectConfigurationReporter.printCurrentValues(logger::info, maskedRawPropertyValues, additionalNotes);
    }

    // Check for options that are just plain bad, ie giving a detector type we don't know about.
    public Optional<DetectUserFriendlyException> validateForPropertyParseErrors() throws IllegalAccessException {
        Map<String, List<String>> errorMap = detectConfigurationReporter.findPropertyParseErrors(DetectProperties.allProperties().getProperties());
        if (!errorMap.isEmpty()) {
            Map.Entry<String, List<String>> entry = errorMap.entrySet().iterator().next();
            return Optional.of(new DetectUserFriendlyException(entry.getKey() + ": " + entry.getValue().get(0), ExitCodeType.FAILURE_GENERAL_ERROR));
        }
        return Optional.empty();
    }
}
