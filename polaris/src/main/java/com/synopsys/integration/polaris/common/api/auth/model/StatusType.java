/*
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class StatusType extends PolarisComponent {
    @SerializedName("statusCode")
    private Integer statusCode;

    @SerializedName("reasonPhrase")
    private String reasonPhrase;

    /**
     * Gets or Sets family
     */
    @JsonAdapter(FamilyEnum.Adapter.class)
    public enum FamilyEnum {
        INFORMATIONAL("INFORMATIONAL"),

        SUCCESSFUL("SUCCESSFUL"),

        REDIRECTION("REDIRECTION"),

        CLIENT_ERROR("CLIENT_ERROR"),

        SERVER_ERROR("SERVER_ERROR"),

        OTHER("OTHER");

        private final String value;

        FamilyEnum(final String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }

        public static FamilyEnum fromValue(final String text) {
            for (final FamilyEnum b : FamilyEnum.values()) {
                if (String.valueOf(b.value).equals(text)) {
                    return b;
                }
            }
            return null;
        }

        public static class Adapter extends TypeAdapter<FamilyEnum> {
            @Override
            public void write(final JsonWriter jsonWriter, final FamilyEnum enumeration) throws IOException {
                jsonWriter.value(enumeration.getValue());
            }

            @Override
            public FamilyEnum read(final JsonReader jsonReader) throws IOException {
                final String value = jsonReader.nextString();
                return FamilyEnum.fromValue(String.valueOf(value));
            }
        }
    }

    @SerializedName("family")
    private FamilyEnum family;

    /**
     * Get statusCode
     * @return statusCode
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(final Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Get reasonPhrase
     * @return reasonPhrase
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(final String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * Get family
     * @return family
     */
    public FamilyEnum getFamily() {
        return family;
    }

    public void setFamily(final FamilyEnum family) {
        this.family = family;
    }

}

