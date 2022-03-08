package com.synopsys.integration.detect.workflow.blackduck.project.customfields;

import java.util.List;

public class CustomFieldOperation {
    public final CustomFieldView customField;
    public final List<String> values;

    public CustomFieldOperation(CustomFieldView customField, List<String> values) {
        this.customField = customField;
        this.values = values;
    }
}
