package com.synopsys.integration.detect.configuration;

import java.util.Map;

import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;

import com.synopsys.integration.detect.exception.DetectUserFriendlyException;
import com.synopsys.integration.detect.exitcode.ExitCodeType;
import com.synopsys.integration.detect.workflow.blackduck.CustomFieldDocument;

public class DetectCustomFieldParser {

    public CustomFieldDocument parseCustomFieldDocument(Map<String, String> currentProperties) throws DetectUserFriendlyException {
        try {
            ConfigurationPropertySource source = new MapConfigurationPropertySource(currentProperties);
            Binder objectBinder = new Binder(source);
            BindResult<CustomFieldDocument> fieldDocumentBinding = objectBinder.bind("detect.custom.fields", CustomFieldDocument.class);
            return fieldDocumentBinding.orElse(new CustomFieldDocument());
        } catch (Exception e) {
            throw new DetectUserFriendlyException("Unable to parse custom fields.", e, ExitCodeType.FAILURE_CONFIGURATION);
        }
    }
}
