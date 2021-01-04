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
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class License extends PolarisComponent {
    @SerializedName("id")
    private UUID id;

    @SerializedName("name")
    private String name;

    @SerializedName("organization")
    private String organization;

    /**
     * Gets or Sets type
     */
    @JsonAdapter(TypeEnum.Adapter.class)
    public enum TypeEnum {
        PAID("PAID"),

        FREE("FREE");

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

    @SerializedName("revoked")
    private Boolean revoked;

    @SerializedName("limits")
    private final Map<String, PeriodOfUse> limits = new HashMap<>();

    @SerializedName("issued-date")
    private OffsetDateTime issuedDate;

    @SerializedName("issued-to")
    private String issuedTo;

    @SerializedName("claimed-date")
    private OffsetDateTime claimedDate;

    @SerializedName("issued-by")
    private String issuedBy;

    @SerializedName("seat-count")
    private Integer seatCount;

    @SerializedName("revoked-date")
    private OffsetDateTime revokedDate;

    /**
     * Get id
     * @return id
     */
    public UUID getId() {
        return id;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get organization
     * @return organization
     */
    public String getOrganization() {
        return organization;
    }

    public void setOrganization(final String organization) {
        this.organization = organization;
    }

    /**
     * Get type
     * @return type
     */
    public TypeEnum getType() {
        return type;
    }

    /**
     * Get revoked
     * @return revoked
     */
    public Boolean getRevoked() {
        return revoked;
    }

    /**
     * Get limits
     * @return limits
     */
    public Map<String, PeriodOfUse> getLimits() {
        return limits;
    }

    /**
     * Get issuedDate
     * @return issuedDate
     */
    public OffsetDateTime getIssuedDate() {
        return issuedDate;
    }

    /**
     * Get issuedTo
     * @return issuedTo
     */
    public String getIssuedTo() {
        return issuedTo;
    }

    /**
     * Get claimedDate
     * @return claimedDate
     */
    public OffsetDateTime getClaimedDate() {
        return claimedDate;
    }

    /**
     * Get issuedBy
     * @return issuedBy
     */
    public String getIssuedBy() {
        return issuedBy;
    }

    /**
     * Get seatCount
     * minimum: 0
     * @return seatCount
     */
    public Integer getSeatCount() {
        return seatCount;
    }

    /**
     * Get revokedDate
     * @return revokedDate
     */
    public OffsetDateTime getRevokedDate() {
        return revokedDate;
    }

}

