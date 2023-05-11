/*
 * Copyright (C) 2023 Synopsys Inc.
 * http://www.synopsys.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Synopsys ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Synopsys.
 */
package com.synopsys.integration.detect.fastsca.model;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;

/**
 * fastSCA report.
 * 
 * Serialization order puts the report version field first as an optimization for version compatibility checks.
 * 
 * @author skatzman
 */
@JsonPropertyOrder(value = { "version", "identifications", "meta" })
public class FastScaReport {
    private final List<FastScaIdentification> identifications;

    private final FastScaMetadata meta;

    @JsonCreator
    public FastScaReport(@JsonProperty("identifications") Collection<FastScaIdentification> identifications,
            @JsonProperty("meta") FastScaMetadata meta) {
        this.identifications = (identifications != null) ? ImmutableList.copyOf(identifications) : ImmutableList.of();
        this.meta = meta;
    }

    /**
     * Gets the identifications.
     * 
     * @return Returns the identification.
     */
    public List<FastScaIdentification> getIdentifications() {
        return identifications;
    }

    /**
     * Gets the metadata.
     * 
     * @return Returns the metadata.
     */
    public FastScaMetadata getMeta() {
        return meta;
    }

    /**
     * Gets the report version.
     * 
     * @return Returns the report version.
     */
    @JsonProperty("version")
    public String getVersion() {
        return "1";
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIdentifications(), getMeta(), getVersion());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaReport) {
            FastScaReport otherFastScaReport = (FastScaReport) otherObject;

            return Objects.equals(getIdentifications(), otherFastScaReport.getIdentifications())
                    && Objects.equals(getMeta(), otherFastScaReport.getMeta())
                    && Objects.equals(getVersion(), otherFastScaReport.getVersion());
        }

        return false;
    }
}
