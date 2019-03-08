package com.synopsys.integration.detectable;

public class ExtractionMetadata<T> {
    final private String key;

    public ExtractionMetadata(final String key, final Class<T> metadataClass) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public int hashCode() {
        return key.hashCode();
    }
}
