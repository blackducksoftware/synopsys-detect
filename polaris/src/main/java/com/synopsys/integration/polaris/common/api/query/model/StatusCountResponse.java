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
package com.synopsys.integration.polaris.common.api.query.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class StatusCountResponse extends PolarisComponent {
    /**
     * Either Open, or Closed
     */
    @JsonAdapter(NameEnum.Adapter.class)
    public enum NameEnum {
        OPEN("Open"),

        CLOSED("Closed");

        private final String value;

        NameEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static NameEnum fromValue(final String text) {
            for (final NameEnum b : NameEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<NameEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final NameEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public NameEnum read(final JsonReader jsonReader) throws IOException {
                final String value = jsonReader.nextString();
                return NameEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("name")
    private NameEnum name;

    @SerializedName("data")
    private List<List<String>> data = new ArrayList<>();

    /**
     * Either Open, or Closed
     * @return name
     */
    public NameEnum getName() {
        return name;
    }

    public void setName(final NameEnum name) {
        this.name = name;
    }

    public StatusCountResponse addDataItem(final List<String> dataItem) {
        this.data.add(dataItem);
        return this;
    }

    /**
     * An array of tuples of corresponding dates and counts.
     * @return data
     */
    public List<List<String>> getData() {
        return data;
    }

    public void setData(final List<List<String>> data) {
        this.data = data;
    }

}

