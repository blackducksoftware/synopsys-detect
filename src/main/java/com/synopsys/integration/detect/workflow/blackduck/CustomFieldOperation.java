/**
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.workflow.blackduck;

import java.util.List;

public class CustomFieldOperation {
    public final CustomFieldView customField;
    public final List<String> values;

    public CustomFieldOperation(final CustomFieldView customField, final List<String> values) {
        this.customField = customField;
        this.values = values;
    }
}
