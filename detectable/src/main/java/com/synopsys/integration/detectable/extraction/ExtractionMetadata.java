/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
