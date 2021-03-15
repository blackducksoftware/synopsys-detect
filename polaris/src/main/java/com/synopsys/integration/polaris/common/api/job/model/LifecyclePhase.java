/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.job.model;

import java.io.IOException;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class LifecyclePhase extends PolarisComponent {
    @SerializedName("phase")
    private PhaseEnum phase;
    @SerializedName("aggregation")
    private AggregationEnum aggregation;
    @SerializedName("durationMillis")
    private Long durationMillis;

    /**
     * Get phase
     * @return phase
     */
    public PhaseEnum getPhase() {
        return phase;
    }

    /**
     * Get aggregation
     * @return aggregation
     */
    public AggregationEnum getAggregation() {
        return aggregation;
    }

    /**
     * Get durationMillis
     * @return durationMillis
     */
    public Long getDurationMillis() {
        return durationMillis;
    }

    /**
     * Gets or Sets phase
     */
    @JsonAdapter(PhaseEnum.Adapter.class)
    public enum PhaseEnum {
        IDIRUPLOADDURATION("idirUploadDuration"),

        JOBQUEUEDDURATION("jobQueuedDuration"),

        JOBDISPATCHDURATION("jobDispatchDuration"),

        IDIRDOWNLOADDURATION("idirDownloadDuration"),

        COVANALYZEDURATION("covAnalyzeDuration"),

        POSTPROCESSDURATION("postProcessDuration"),

        RESULTSUPLOADDURATION("resultsUploadDuration"),

        TDSUPLOADDURATION("tdsUploadDuration"),

        GENERATEREPORTDURATION("generateReportDuration");

        private String value;

        PhaseEnum(String value) {
            this.value = value;
        }

        public static PhaseEnum fromValue(String text) {
            for (PhaseEnum b : PhaseEnum.values()) {
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

        public static class Adapter extends TypeAdapter<PhaseEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final PhaseEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public PhaseEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return PhaseEnum.fromValue(String.valueOf(value));
            }
        }
    }

    /**
     * Gets or Sets aggregation
     */
    @JsonAdapter(AggregationEnum.Adapter.class)
    public enum AggregationEnum {
        TOTAL("total"),

        MEAN("mean");

        private String value;

        AggregationEnum(String value) {
            this.value = value;
        }

        public static AggregationEnum fromValue(String text) {
            for (AggregationEnum b : AggregationEnum.values()) {
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

        public static class Adapter extends TypeAdapter<AggregationEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final AggregationEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public AggregationEnum read(final JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return AggregationEnum.fromValue(String.valueOf(value));
            }
        }
    }

}
