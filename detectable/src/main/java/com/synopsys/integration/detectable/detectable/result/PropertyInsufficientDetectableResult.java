package com.synopsys.integration.detectable.detectable.result;

import org.apache.commons.lang3.StringUtils;

@Deprecated
public class PropertyInsufficientDetectableResult extends FailedDetectableResult {
    private static final String PREFIX = "The properties are insufficient to run. ";

    public PropertyInsufficientDetectableResult() {
        super(PREFIX);
    }

    public PropertyInsufficientDetectableResult(String message) {
        super(PREFIX , StringUtils.trimToEmpty(message));
    }
}
