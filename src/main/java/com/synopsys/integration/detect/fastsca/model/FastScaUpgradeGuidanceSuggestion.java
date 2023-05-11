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
import com.synopsys.kb.httpclient.model.RiskProfile;
import com.synopsys.kb.httpclient.model.UpgradeGuidanceSuggestion;

/**
 * fastSCA upgrade guidance suggestion.
 * 
 * Included in the fastSCA report to represent short-term or long-term suggestions for component version upgrade
 * guidance or component origin upgrade guidance when available.
 * 
 * @author skatzman
 */
public class FastScaUpgradeGuidanceSuggestion {
    private final UUID componentVersionId;

    private final UUID componentOriginId;

    private final String componentVersionName;

    private final String componentOriginExternalNamespace;

    private final String componentOriginExternalId;

    private final RiskProfile riskProfile;

    @JsonCreator
    public FastScaUpgradeGuidanceSuggestion(@JsonProperty("componentVersionId") UUID componentVersionId,
            @JsonProperty("componentOriginId") UUID componentOriginId,
            @JsonProperty("componentVersionName") String componentVersionName,
            @JsonProperty("componentOriginExternalNamespace") String componentOriginExternalNamespace,
            @JsonProperty("componentOriginExternalId") String componentOriginExternalId,
            @JsonProperty("riskProfile") RiskProfile riskProfile) {
        this.componentVersionId = componentVersionId;
        this.componentOriginId = componentOriginId;
        this.componentVersionName = componentVersionName;
        this.componentOriginExternalNamespace = componentOriginExternalNamespace;
        this.componentOriginExternalId = componentOriginExternalId;
        this.riskProfile = riskProfile;
    }

    public FastScaUpgradeGuidanceSuggestion(UpgradeGuidanceSuggestion upgradeGuidanceSuggestion) {
    	Objects.requireNonNull(upgradeGuidanceSuggestion, "Upgrade guidance suggestion must be initialized.");
    	
    	this.componentVersionId = upgradeGuidanceSuggestion.getVersionId().orElse(null);
    	this.componentOriginId = upgradeGuidanceSuggestion.getVariantId().orElse(null);
    	this.componentVersionName = upgradeGuidanceSuggestion.getVersionName();
    	this.componentOriginExternalNamespace = upgradeGuidanceSuggestion.getVariantExternalNamespace().orElse(null);
    	this.componentOriginExternalId = upgradeGuidanceSuggestion.getVariantExternalId();
    	this.riskProfile = upgradeGuidanceSuggestion.getRiskProfile();
    }
    
    /**
     * Gets the component version id.
     * 
     * @return Returns the component version id.
     */
    public UUID getComponentVersionId() {
        return componentVersionId;
    }

    /**
     * Gets the component origin id.
     * 
     * @return Returns the component origin id.
     */
    public Optional<UUID> getComponentOriginId() {
        return Optional.ofNullable(componentOriginId);
    }

    /**
     * Gets the component version name.
     * 
     * @return Returns the component version name.
     */
    public String getComponentVersionName() {
        return componentVersionName;
    }

    /**
     * Gets the component origin external namespace.
     * 
     * @return Returns the component origin external namespace.
     */
    public Optional<String> getComponentOriginExternalNamespace() {
        return Optional.ofNullable(componentOriginExternalNamespace);
    }

    /**
     * Gets the component origin external id.
     * 
     * @return Returns the component origin external id.
     */
    public Optional<String> getComponentOriginExternalId() {
        return Optional.ofNullable(componentOriginExternalId);
    }

    /**
     * Gets the risk profile.
     * 
     * @return Returns the risk profile.
     */
    public RiskProfile getRiskProfile() {
        return riskProfile;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentVersionId(), getComponentOriginId(), getComponentVersionName(), getComponentOriginExternalNamespace(),
                getComponentOriginExternalId(), getRiskProfile());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaUpgradeGuidanceSuggestion) {
            FastScaUpgradeGuidanceSuggestion otherFastScaUpgradeGuidanceSuggestion = (FastScaUpgradeGuidanceSuggestion) otherObject;

            return Objects.equals(getComponentVersionId(), otherFastScaUpgradeGuidanceSuggestion.getComponentVersionId())
                    && Objects.equals(getComponentOriginId(), otherFastScaUpgradeGuidanceSuggestion.getComponentOriginId())
                    && Objects.equals(getComponentVersionName(), otherFastScaUpgradeGuidanceSuggestion.getComponentVersionName())
                    && Objects.equals(getComponentOriginExternalNamespace(), otherFastScaUpgradeGuidanceSuggestion.getComponentOriginExternalNamespace())
                    && Objects.equals(getComponentOriginExternalId(), otherFastScaUpgradeGuidanceSuggestion.getComponentOriginExternalId())
                    && Objects.equals(getRiskProfile(), otherFastScaUpgradeGuidanceSuggestion.getRiskProfile());
        }

        return false;
    }
}
