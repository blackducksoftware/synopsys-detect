package com.synopsys.integration.detectable.detectable.result;

import org.apache.commons.lang3.StringUtils;

public class PropertyInsufficientDetectableResult extends FailedDetectableResult {
    private final String message;

    public PropertyInsufficientDetectableResult() {
        this(null);
    }

    public PropertyInsufficientDetectableResult(String message) {
        this.message = message;
    }

    @Override
    public String toDescription() {
        return "The properties are insufficient to run. " + StringUtils.trimToEmpty(message);
    }
}
