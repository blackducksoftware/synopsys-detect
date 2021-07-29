/*
 * synopsys-detect
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.configuration.validation;

import java.util.List;
import java.util.Map;

public class DeprecationResult {
    private final Map<String, String> additionalNotes;
    private final Map<String, List<String>> deprecationMessages;

    public DeprecationResult(Map<String, String> additionalNotes, Map<String, List<String>> deprecationMessages) {
        this.additionalNotes = additionalNotes;
        this.deprecationMessages = deprecationMessages;
    }

    public Map<String, String> getAdditionalNotes() {
        return additionalNotes;
    }

    public Map<String, List<String>> getDeprecationMessages() {
        return deprecationMessages;
    }
}