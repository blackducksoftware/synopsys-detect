package com.synopsys.integration.detect.configuration.validation;

import java.util.List;
import java.util.Map;

import com.synopsys.integration.configuration.config.PropertyConfiguration;
import com.synopsys.integration.configuration.help.PropertyConfigurationHelpContext;

public class DetectConfigurationState {
    private final Map<String, String> additionalNotes;
    private final Map<String, List<String>> deprecationMessages;
    private final PropertyConfigurationHelpContext detectConfigurationReporter;
    private final boolean hasNotUsedFailureProperties;

    public DetectConfigurationState(PropertyConfiguration detectConfiguration, Map<String, String> additionalNotes, Map<String, List<String>> deprecationMessages, boolean hasNotUsedFailureProperties) {
        this.additionalNotes = additionalNotes;
        this.deprecationMessages = deprecationMessages;
        this.hasNotUsedFailureProperties = hasNotUsedFailureProperties;
        this.detectConfigurationReporter = new PropertyConfigurationHelpContext(detectConfiguration);
    }

    public Map<String, String> getAdditionalNotes() {
        return additionalNotes;
    }

    public Map<String, List<String>> getDeprecationMessages() {
        return deprecationMessages;
    }

    public PropertyConfigurationHelpContext getDetectConfigurationReporter() {
        return detectConfigurationReporter;
    }

    public boolean hasNotUsedFailureProperties() {
        return hasNotUsedFailureProperties;
    }

}