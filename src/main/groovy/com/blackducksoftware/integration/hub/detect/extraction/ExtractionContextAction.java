package com.blackducksoftware.integration.hub.detect.extraction;

public interface ExtractionContextAction<C extends ExtractionContext, V> {

    void perform(C context, V value);

}
