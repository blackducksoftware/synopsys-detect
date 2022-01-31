package com.synopsys.integration.detect.configuration;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import com.synopsys.integration.detect.configuration.enumeration.ExitCodeType;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldDocument;
import com.synopsys.integration.detect.workflow.blackduck.project.customfields.CustomFieldElement;

public class DetectCustomFieldParser {

    public CustomFieldDocument parseCustomFieldDocument(Map<String, String> currentProperties) throws DetectUserFriendlyException {
        try {
            ConfigurationPropertySource source = new MapConfigurationPropertySource(currentProperties);
            Binder objectBinder = new Binder(source);
            BindResult<CustomFieldDocument> fieldDocumentBinding = objectBinder.bind("detect.custom.fields", CustomFieldDocument.class);
            CustomFieldDocument fieldDocument = fieldDocumentBinding.orElse(new CustomFieldDocument());
            fieldDocument.getProject().forEach(this::filterEmptyQuotes);
            fieldDocument.getVersion().forEach(this::filterEmptyQuotes);
            return fieldDocument;
        } catch (Exception e) {
            throw new DetectUserFriendlyException("Unable to parse custom fields.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }

    public void filterEmptyQuotes(CustomFieldElement element) {
        element.setValue(element.getValue().stream().filter(value -> !("\"\"".equals(value) || "''".equals(value))).collect(Collectors.toList()));
    }
}
