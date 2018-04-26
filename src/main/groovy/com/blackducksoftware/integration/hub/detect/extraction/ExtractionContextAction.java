package com.blackducksoftware.integration.hub.detect.extraction.bucket;

import com.blackducksoftware.integration.hub.detect.extraction.ExtractionContext;

public interface RequirementAction<C extends ExtractionContext, V> {

    void perform(C context, V value);

}
