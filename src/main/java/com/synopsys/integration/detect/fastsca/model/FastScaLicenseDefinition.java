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

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.synopsys.kb.httpclient.model.LicenseDefinitionType;

/**
 * fastSCA license definition.
 * 
 * Included in the fastSCA report within a component version.
 * 
 * @author skatzman
 */
public class FastScaLicenseDefinition {
    @Nullable
    private final FastScaLicense license;

    @Nullable
    private final LicenseDefinitionType type;

    private final List<FastScaLicenseDefinition> licenseDefinitions;

    @JsonCreator
    public FastScaLicenseDefinition(@JsonProperty("license") FastScaLicense license,
            @JsonProperty("type") LicenseDefinitionType type,
            @JsonProperty("licenseDefinitions") List<FastScaLicenseDefinition> licenseDefinitions) {
        this.license = license;
        this.type = type;
        this.licenseDefinitions = (licenseDefinitions != null) ? ImmutableList.copyOf(licenseDefinitions) : ImmutableList.of();
    }

    public FastScaLicenseDefinition(FastScaLicense license) {
        this.license = Objects.requireNonNull(license, "License must be initialized.");
        this.type = null;
        this.licenseDefinitions = ImmutableList.of();
    }

    public FastScaLicenseDefinition(LicenseDefinitionType type, List<FastScaLicenseDefinition> licenseDefinitions) {
        this.license = null;
        this.type = Objects.requireNonNull(type, "Type must be initialized.");

        Objects.requireNonNull(licenseDefinitions, "License definitions must be initialized.");
        Preconditions.checkArgument(licenseDefinitions.size() > 1, "License definitions must contains more than 1 license definition.");
        this.licenseDefinitions = ImmutableList.copyOf(licenseDefinitions);
    }
    
    /**
     * Gets the license.
     * 
     * @return Returns the license.
     */
    public Optional<FastScaLicense> getLicense() {
        return Optional.ofNullable(license);
    }

    /**
     * Gets the type.
     * 
     * @return Returns the type.
     */
    public Optional<LicenseDefinitionType> getType() {
        return Optional.ofNullable(type);
    }

    /**
     * Gets the license definitions.
     * 
     * @return Returns the license definitions.
     */
    public List<FastScaLicenseDefinition> getLicenseDefinitions() {
        return licenseDefinitions;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLicense(), getType(), getLicenseDefinitions());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaLicenseDefinition) {
            FastScaLicenseDefinition otherFastScaLicenseDefinition = (FastScaLicenseDefinition) otherObject;

            return Objects.equals(getLicense(), otherFastScaLicenseDefinition.getLicense())
                    && Objects.equals(getType(), otherFastScaLicenseDefinition.getType())
                    && Objects.equals(getLicenseDefinitions(), otherFastScaLicenseDefinition.getLicenseDefinitions());
        }

        return false;
    }
}
