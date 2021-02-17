/*
 * polaris
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
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class SamlSignatureHashAlgorithmList extends PolarisComponent {
    @SerializedName("meta")
    private PagedMeta meta = null;

    /**
     * Gets or Sets algorithms
     */
    @JsonAdapter(AlgorithmsEnum.Adapter.class)
    public enum AlgorithmsEnum {
        SHA1("SHA1"),

        SHA256("SHA256"),

        SHA512("SHA512");

        private final String value;

        AlgorithmsEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static AlgorithmsEnum fromValue(final String text) {
            for (final AlgorithmsEnum b : AlgorithmsEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<AlgorithmsEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final AlgorithmsEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public AlgorithmsEnum read(final JsonReader jsonReader) throws IOException {
                final String value = jsonReader.nextString();
                return AlgorithmsEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("algorithms")
    private final List<AlgorithmsEnum> algorithms = null;

    /**
     * Get meta
     * @return meta
     */
    public PagedMeta getMeta() {
        return meta;
    }

    public void setMeta(final PagedMeta meta) {
        this.meta = meta;
    }

    /**
     * Get algorithms
     * @return algorithms
     */
    public List<AlgorithmsEnum> getAlgorithms() {
        return algorithms;
    }

}

