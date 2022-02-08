package com.synopsys.integration.detectable.extraction;

import java.util.Objects;

public class ExtractionMetadata<T> {
    final private String key;
    private final Class<T> metadataClass;

    public ExtractionMetadata(String key, Class<T> metadataClass) {
        this.key = key;
        this.metadataClass = metadataClass;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExtractionMetadata<?> that = (ExtractionMetadata<?>) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    public Class<T> getMetadataClass() {
        return metadataClass;
    }
}
