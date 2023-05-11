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

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.bd.kb.httpclient.model.BdLicense;
import com.synopsys.kb.httpclient.model.LicenseCodeSharing;
import com.synopsys.kb.httpclient.model.LicenseOwnership;
import com.synopsys.kb.httpclient.model.LicenseRestriction;

/**
 * fastSCA license.
 * 
 * Included in the fastSCA report within a component version license definition.
 * 
 * @author skatzman
 */
public class FastScaLicense {
    private final UUID id;

    private final String name;

    private final LicenseCodeSharing codeSharing;

    private final LicenseOwnership ownership;

    private final String spdxId;

    private final LicenseRestriction restriction;

    @JsonCreator
    public FastScaLicense(@JsonProperty("id") UUID id,
            @JsonProperty("name") String name,
            @JsonProperty("codeSharing") LicenseCodeSharing codeSharing,
            @JsonProperty("ownership") LicenseOwnership ownership,
            @JsonProperty("spdxId") String spdxId,
            @JsonProperty("restriction") LicenseRestriction restriction) {
        this.id = id;
        this.name = name;
        this.codeSharing = codeSharing;
        this.ownership = ownership;
        this.spdxId = spdxId;
        this.restriction = restriction;
    }

    public FastScaLicense(BdLicense bdLicense) {
    	Objects.requireNonNull(bdLicense, "BD license must be initialized.");
    	
    	this.id = bdLicense.getId();
    	this.name = bdLicense.getName();
    	this.codeSharing = bdLicense.getCodeSharing();
    	this.ownership = bdLicense.getOwnership();
    	this.spdxId = bdLicense.getSpdxId().orElse(null);
    	this.restriction = bdLicense.getRestriction();
    }
    
    /**
     * Gets the license id.
     * 
     * @return Returns the license id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the license name.
     * 
     * @return Returns the license name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the license code sharing.
     * 
     * @return Returns the license code sharing.
     */
    public LicenseCodeSharing getCodeSharing() {
        return codeSharing;
    }

    /**
     * Gets the license ownership.
     * 
     * @return Returns the license ownership.
     */
    public LicenseOwnership getOwnership() {
        return ownership;
    }

    /**
     * Gets the SPDX license id.
     * 
     * @return Returns the SPDX license id.
     */
    public Optional<String> getSpdxId() {
        return Optional.ofNullable(spdxId);
    }

    /**
     * Gets the license restriction.
     * 
     * @return Returns the license restriction.
     */
    public LicenseRestriction getRestriction() {
        return restriction;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCodeSharing(), getOwnership(), getSpdxId(), getRestriction());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaLicense) {
            FastScaLicense otherFastScaLicense = (FastScaLicense) otherObject;

            return Objects.equals(getId(), otherFastScaLicense.getId())
                    && Objects.equals(getName(), otherFastScaLicense.getName())
                    && Objects.equals(getCodeSharing(), otherFastScaLicense.getCodeSharing())
                    && Objects.equals(getOwnership(), otherFastScaLicense.getOwnership())
                    && Objects.equals(getSpdxId(), otherFastScaLicense.getSpdxId())
                    && Objects.equals(getRestriction(), otherFastScaLicense.getRestriction());
        }

        return false;
    }
}
