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

public class RunV0Attributes extends PolarisComponent {
    /**
     * &#x60;Mutable&#x60;
     */
    @JsonAdapter(StatusEnum.Adapter.class)
    public enum StatusEnum {
        INCOMPLETE("Incomplete"),

        COMPLETE("Complete");

        private final String value;

        StatusEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static StatusEnum fromValue(final String text) {
            for (final StatusEnum b : StatusEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<StatusEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final StatusEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public StatusEnum read(final JsonReader jsonReader) throws IOException {
                final String value = jsonReader.nextString();
                return StatusEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("status")
    private StatusEnum status = StatusEnum.INCOMPLETE;

    @SerializedName("creation-date")
    private String creationDate;

    @SerializedName("completed-date")
    private String completedDate;

    @SerializedName("segment")
    private Boolean segment = false;

    @SerializedName("upload-id")
    private String uploadId;

    @SerializedName("run-type")
    private String runType;

    @SerializedName("fingerprints")
    private List<String> fingerprints = new ArrayList<>();

    /**
     * &#x60;Mutable&#x60;
     * @return status
     */
    public StatusEnum getStatus() {
        return status;
    }

    public void setStatus(final StatusEnum status) {
        this.status = status;
    }

    /**
     * &#x60;Automatic&#x60; The date (in UTC) when this run was added to SWIP.
     * @return creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(final String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * &#x60;Automatic&#x60; The date (in UTC) when this run was made complete.
     * @return completedDate
     */
    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(final String completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * If the run is a segment in a stream of runs.
     * @return segment
     */
    public Boolean getSegment() {
        return segment;
    }

    public void setSegment(final Boolean segment) {
        this.segment = segment;
    }

    /**
     * The upload id of the run
     * @return uploadId
     */
    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(final String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * The run type of the run
     * @return runType
     */
    public String getRunType() {
        return runType;
    }

    public void setRunType(final String runType) {
        this.runType = runType;
    }

    public RunV0Attributes addFingerprintsItem(final String fingerprintsItem) {
        this.fingerprints.add(fingerprintsItem);
        return this;
    }

    /**
     * &#x60;Required&#x60; The fingerprints used by this run.
     * @return fingerprints
     */
    public List<String> getFingerprints() {
        return fingerprints;
    }

    public void setFingerprints(final List<String> fingerprints) {
        this.fingerprints = fingerprints;
    }

}

