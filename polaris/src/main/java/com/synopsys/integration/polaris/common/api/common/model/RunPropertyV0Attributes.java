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
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RunPropertyV0Attributes extends PolarisComponent {
    /**
     * The type of the property.  &#x60;Required&#x60;.
     */
    @JsonAdapter(TypeEnum.Adapter.class)
    public enum TypeEnum {
        STRING("string"),

        STRING_LIST("string_list"),

        INT64("int64"),

        INT64_LIST("int64_list"),

        REFERENCE("reference"),

        REFERENCE_LIST("reference_list"),

        INTERVAL("interval");

        private final String value;

        TypeEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static TypeEnum fromValue(final String text) {
            for (final TypeEnum b : TypeEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<TypeEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final TypeEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public TypeEnum read(final JsonReader jsonReader) throws IOException {
                final String value = jsonReader.nextString();
                return TypeEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("type")
    private TypeEnum type;

    @SerializedName("property-name")
    private String propertyName;

    @SerializedName("namespace")
    private String namespace;

    @SerializedName("start-timestamp")
    private String startTimestamp;

    @SerializedName("end-timestamp")
    private String endTimestamp;

    @SerializedName("duration")
    private String duration;

    @SerializedName("string-value")
    private String stringValue;

    @SerializedName("string-list-value")
    private List<String> stringListValue = null;

    @SerializedName("reference-value")
    private String referenceValue;

    @SerializedName("reference-list-value")
    private List<String> referenceListValue = null;

    @SerializedName("integer-value")
    private Long integerValue;

    @SerializedName("integer-list-value")
    private List<Long> integerListValue = null;

    /**
     * The type of the property.  &#x60;Required&#x60;.
     * @return type
     */
    public TypeEnum getType() {
        return type;
    }

    public void setType(final TypeEnum type) {
        this.type = type;
    }

    /**
     * The name of the property.  &#x60;Required&#x60;.
     * @return propertyName
     */
    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(final String propertyName) {
        this.propertyName = propertyName;
    }

    /**
     * The namespace of the property.  &#x60;Required&#x60;.
     * @return namespace
     */
    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;interval&#x60;) The start timestamp of the property for the run, formatted in ISO 8601 format with time zone.
     * @return startTimestamp
     */
    public String getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(final String startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;interval&#x60;) The end timestamp of the property for the run, formatted in ISO 8601 format with time zone.
     * @return endTimestamp
     */
    public String getEndTimestamp() {
        return endTimestamp;
    }

    public void setEndTimestamp(final String endTimestamp) {
        this.endTimestamp = endTimestamp;
    }

    /**
     * &#x60;Automatic&#x60; (iff the property type is &#x60;interval&#x60;) The duration, in ISO 8601 format, between the start and end timestamp.
     * @return duration
     */
    public String getDuration() {
        return duration;
    }

    public void setDuration(final String duration) {
        this.duration = duration;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;string&#x60;) The string value of the property for the run.
     * @return stringValue
     */
    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(final String stringValue) {
        this.stringValue = stringValue;
    }

    public RunPropertyV0Attributes addStringListValueItem(final String stringListValueItem) {
        if (this.stringListValue == null) {
            this.stringListValue = new ArrayList<>();
        }
        this.stringListValue.add(stringListValueItem);
        return this;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;string_list&#x60;) The string list value of the property for the run.
     * @return stringListValue
     */
    public List<String> getStringListValue() {
        return stringListValue;
    }

    public void setStringListValue(final List<String> stringListValue) {
        this.stringListValue = stringListValue;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;reference&#x60;) The reference value of the property for the run.
     * @return referenceValue
     */
    public String getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(final String referenceValue) {
        this.referenceValue = referenceValue;
    }

    public RunPropertyV0Attributes addReferenceListValueItem(final String referenceListValueItem) {
        if (this.referenceListValue == null) {
            this.referenceListValue = new ArrayList<>();
        }
        this.referenceListValue.add(referenceListValueItem);
        return this;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;reference_list&#x60;) The reference list value of the property for the run.
     * @return referenceListValue
     */
    public List<String> getReferenceListValue() {
        return referenceListValue;
    }

    public void setReferenceListValue(final List<String> referenceListValue) {
        this.referenceListValue = referenceListValue;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;int64&#x60;) The integer value (stored as a signed 64-bit integer) of the property for the run.
     * @return integerValue
     */
    public Long getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(final Long integerValue) {
        this.integerValue = integerValue;
    }

    public RunPropertyV0Attributes addIntegerListValueItem(final Long integerListValueItem) {
        if (this.integerListValue == null) {
            this.integerListValue = new ArrayList<>();
        }
        this.integerListValue.add(integerListValueItem);
        return this;
    }

    /**
     * &#x60;Required&#x60; (iff the property type is &#x60;int64_list&#x60;) The integer list value (stored as a signed 64-bit integer) of the property for the run.
     * @return integerListValue
     */
    public List<Long> getIntegerListValue() {
        return integerListValue;
    }

    public void setIntegerListValue(final List<Long> integerListValue) {
        this.integerListValue = integerListValue;
    }

}

