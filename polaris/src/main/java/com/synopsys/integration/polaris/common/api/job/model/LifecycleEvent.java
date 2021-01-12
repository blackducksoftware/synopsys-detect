/**
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
package com.synopsys.integration.polaris.common.api.job.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class LifecycleEvent extends PolarisComponent {
    @SerializedName("id")
    private String id;
    @SerializedName("key")
    private KeyEnum key;
    @SerializedName("context")
    private Map<String, String> context = new HashMap<>();
    @SerializedName("timestamp")
    private String timestamp;

    /**
     * Get id
     * @return id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get key
     * @return key
     */
    public KeyEnum getKey() {
        return key;
    }

    /**
     * Get context
     * @return context
     */
    public Map<String, String> getContext() {
        return context;
    }

    /**
     * Get timestamp
     * @return timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Gets or Sets key
     */
    @JsonAdapter(KeyEnum.Adapter.class)
    public enum KeyEnum {
        IDIRUPLOADSTART("idirUploadStart"),

        IDIRUPLOADFINISH("idirUploadFinish"),

        JOBQUEUEDSTART("jobQueuedStart"),

        JOBQUEUEDFINISH("jobQueuedFinish"),

        JOBDISPATCHSTART("jobDispatchStart"),

        JOBDISPATCHFINISH("jobDispatchFinish"),

        IDIRDOWNLOADSTART("idirDownloadStart"),

        IDIRDOWNLOADFINISH("idirDownloadFinish"),

        COVANALYZESTART("covAnalyzeStart"),

        COVANALYZEFINISH("covAnalyzeFinish"),

        POSTPROCESSSTART("postProcessStart"),

        POSTPROCESSFINISH("postProcessFinish"),

        RESULTSUPLOADSTART("resultsUploadStart"),

        RESULTSUPLOADFINISH("resultsUploadFinish"),

        TDSUPLOADSTART("tdsUploadStart"),

        TDSUPLOADFINISH("tdsUploadFinish"),

        GENERATEREPORTSTART("generateReportStart"),

        GENERATEREPORTFINISH("generateReportFinish");

        private String value;

        KeyEnum(String value) {
            this.value = value;
        }

        public static KeyEnum fromValue(String text) {
            for (KeyEnum b : KeyEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static class Adapter extends TypeAdapter<KeyEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final KeyEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public KeyEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return KeyEnum.fromValue(String.valueOf(value));
            }
        }
    }

}
