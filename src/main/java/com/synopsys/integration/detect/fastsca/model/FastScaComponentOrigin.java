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
import com.synopsys.bd.kb.httpclient.model.BdComponentVariant;

/**
 * fastSCA component origin.
 * 
 * Included in the fastSCA report for matches to component origins.
 * 
 * @author skatzman
 */
public class FastScaComponentOrigin {
    private final UUID id;

    private final String externalNamespace;

    private final String externalId;

    private final boolean isExternalNamespaceDistribution;

    private final String packageUrl;

    @JsonCreator
    public FastScaComponentOrigin(@JsonProperty("id") UUID id,
            @JsonProperty("externalNamespace") String externalNamespace,
            @JsonProperty("externalId") String externalId,
            @JsonProperty("externalNamespaceDistribution") boolean isExternalNamespaceDistribution,
            @JsonProperty("packageUrl") String packageUrl) {
        this.id = id;
        this.externalNamespace = externalNamespace;
        this.externalId = externalId;
        this.isExternalNamespaceDistribution = isExternalNamespaceDistribution;
        this.packageUrl = packageUrl;
    }

    public FastScaComponentOrigin(BdComponentVariant bdComponentVariant) {
    	Objects.requireNonNull(bdComponentVariant, "BD component variant must be initialized.");
    	
    	this.id = bdComponentVariant.getId();
    	this.externalNamespace = bdComponentVariant.getExternalNamespace().orElse(null);
    	this.externalId = bdComponentVariant.getExternalId().orElse(null);
    	this.isExternalNamespaceDistribution = bdComponentVariant.isExternalNamespaceDistribution();
    	this.packageUrl = bdComponentVariant.getPackageUrl().orElse(null);
    }
    
    /**
     * Gets the component origin id.
     * 
     * @return Returns the component origin id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the external namespace.
     * 
     * @return Returns the external namespace.
     */
    public Optional<String> getExternalNamespace() {
        return Optional.ofNullable(externalNamespace);
    }

    /**
     * Gets the external id.
     * 
     * @return Returns the external id.
     */
    public Optional<String> getExternalId() {
        return Optional.ofNullable(externalId);
    }

    /**
     * Gets the external namespace distribution.
     * 
     * @return Returns the external namespace distribution.
     */
    @JsonProperty("externalNamespaceDistribution")
    public boolean isExternalNamespaceDistribution() {
        return isExternalNamespaceDistribution;
    }

    /**
     * Gets the package URL.
     * 
     * @return Returns the package URL.
     */
    public Optional<String> getPackageUrl() {
        return Optional.ofNullable(packageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getExternalNamespace(), getExternalId(), isExternalNamespaceDistribution(), getPackageUrl());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaComponentOrigin) {
            FastScaComponentOrigin otherFastScaComponentOrigin = (FastScaComponentOrigin) otherObject;

            return Objects.equals(getId(), otherFastScaComponentOrigin.getId())
                    && Objects.equals(getExternalNamespace(), otherFastScaComponentOrigin.getExternalNamespace())
                    && Objects.equals(getExternalId(), otherFastScaComponentOrigin.getExternalId())
                    && Objects.equals(isExternalNamespaceDistribution(), otherFastScaComponentOrigin.isExternalNamespaceDistribution())
                    && Objects.equals(getPackageUrl(), otherFastScaComponentOrigin.getPackageUrl());
        }

        return false;
    }
}
