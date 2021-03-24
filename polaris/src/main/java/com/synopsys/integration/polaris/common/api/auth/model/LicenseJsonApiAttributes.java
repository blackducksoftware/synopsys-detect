/**
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
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class LicenseJsonApiAttributes extends PolarisComponent {
    @SerializedName("claimed-date")
    private OffsetDateTime claimedDate;

    @SerializedName("issued-by")
    private String issuedBy;

    @SerializedName("issued-date")
    private OffsetDateTime issuedDate;

    @SerializedName("issued-to")
    private String issuedTo;

    @SerializedName("limits")
    private Map<String, Object> limits = null;

    @SerializedName("name")
    private String name;

    @SerializedName("revoked")
    private Boolean revoked;

    @SerializedName("revoked-date")
    private OffsetDateTime revokedDate;

    @SerializedName("seat-count")
    private Integer seatCount;

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

    /**
     * Get claimedDate
     * @return claimedDate
     */
    public OffsetDateTime getClaimedDate() {
        return claimedDate;
    }

    public void setClaimedDate(final OffsetDateTime claimedDate) {
        this.claimedDate = claimedDate;
    }

    /**
     * Get issuedBy
     * @return issuedBy
     */
    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(final String issuedBy) {
        this.issuedBy = issuedBy;
    }

    /**
     * Get issuedDate
     * @return issuedDate
     */
    public OffsetDateTime getIssuedDate() {
        return issuedDate;
    }

    public void setIssuedDate(final OffsetDateTime issuedDate) {
        this.issuedDate = issuedDate;
    }

    /**
     * Get issuedTo
     * @return issuedTo
     */
    public String getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(final String issuedTo) {
        this.issuedTo = issuedTo;
    }

    public LicenseJsonApiAttributes putLimitsItem(final String key, final Object limitsItem) {
        if (this.limits == null) {
            this.limits = new HashMap<>();
        }
        this.limits.put(key, limitsItem);
        return this;
    }

    /**
     * Get limits
     * @return limits
     */
    public Map<String, Object> getLimits() {
        return limits;
    }

    public void setLimits(final Map<String, Object> limits) {
        this.limits = limits;
    }

    /**
     * Get name
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Get revoked
     * @return revoked
     */
    public Boolean getRevoked() {
        return revoked;
    }

    public void setRevoked(final Boolean revoked) {
        this.revoked = revoked;
    }

    /**
     * Get revokedDate
     * @return revokedDate
     */
    public OffsetDateTime getRevokedDate() {
        return revokedDate;
    }

    public void setRevokedDate(final OffsetDateTime revokedDate) {
        this.revokedDate = revokedDate;
    }

    /**
     * Get seatCount
     * @return seatCount
     */
    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(final Integer seatCount) {
        this.seatCount = seatCount;
    }

    /**
     * Get type
     * @return type
     */
    public TypeEnum getType() {
        return type;
    }

    public void setType(final TypeEnum type) {
        this.type = type;
    }

}

