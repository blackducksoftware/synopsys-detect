/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.extraction;

import java.util.Objects;

public class ExtractionMetadata<T> {
    final private String key;
    private final Class<T> metadataClass;

    public ExtractionMetadata(final String key, final Class<T> metadataClass) {
        this.key = key;
        this.metadataClass = metadataClass;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ExtractionMetadata<?> that = (ExtractionMetadata<?>) o;
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
