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

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.bd.kb.httpclient.model.BdComponentVersion;

/**
 * fastSCA component version.
 * 
 * Included in the fastSCA report for matches to component versions and component origins.
 * 
 * @author skatzman
 */
public class FastScaComponentVersion {
    private final UUID id;

    private final String version;

    private final OffsetDateTime releasedOn;

    private final FastScaLicenseDefinition licenseDefinition;

    @JsonCreator
    public FastScaComponentVersion(@JsonProperty("id") UUID id,
            @JsonProperty("version") String version,
            @JsonProperty("releasedOn") OffsetDateTime releasedOn,
            @JsonProperty("licenseDefinition") FastScaLicenseDefinition licenseDefinition) {
        this.id = id;
        this.version = version;
        this.releasedOn = releasedOn;
        this.licenseDefinition = licenseDefinition;
    }
    
    public FastScaComponentVersion(BdComponentVersion bdComponentVersion, FastScaLicenseDefinition licenseDefinition) {
    	Objects.requireNonNull(bdComponentVersion, "BD component version must be initialized.");
    	Objects.requireNonNull(licenseDefinition, "License definition must be initialized.");
    	
    	this.id = bdComponentVersion.getId();
    	this.version = bdComponentVersion.getVersion().orElse(null);
    	this.releasedOn = bdComponentVersion.getReleasedOn().orElse(null);
    	this.licenseDefinition = licenseDefinition;
    }
    
    /**
     * Gets the component version id.
     * 
     * @return Returns the component version id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the version name.
     * 
     * @return Returns the component version name if present and emptiness otherwise.
     */
    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    /**
     * Gets the released on timestamp.
     * 
     * @return Returns the released on timestamp if present and emptiness otherwise.
     */
    public Optional<OffsetDateTime> getReleasedOn() {
        return Optional.ofNullable(releasedOn);
    }

    /**
     * Gets the declared license definition.
     * 
     * @return Returns the declared license definition.
     */
    public Optional<FastScaLicenseDefinition> getLicenseDefinition() {
        return Optional.ofNullable(licenseDefinition);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getVersion(), getReleasedOn(), getLicenseDefinition());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaComponentVersion) {
            FastScaComponentVersion otherFastScaComponentVersion = (FastScaComponentVersion) otherObject;

            return super.equals(otherObject)
                    && Objects.equals(getId(), otherFastScaComponentVersion.getId())
                    && Objects.equals(getVersion(), otherFastScaComponentVersion.getVersion())
                    && Objects.equals(getReleasedOn(), otherFastScaComponentVersion.getReleasedOn())
                    && Objects.equals(getLicenseDefinition(), otherFastScaComponentVersion.getLicenseDefinition());
        }

        return false;
    }
}
