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

import javax.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.synopsys.kb.httpclient.model.UpgradeGuidance;

/**
 * fastSCA upgrade guidance.
 * 
 * Included in the fastSCA report to represent component version upgrade guidance or component origin upgrade guidance
 * when available.
 * 
 * @author skatzman
 */
public class FastScaUpgradeGuidance {
    private final UUID componentId;

    private final UUID componentVersionId;

    private final UUID componentOriginId;

    private final String componentName;

    private final String componentVersionName;

    @Nullable
    private final String componentOriginExternalNamespace;

    private final String componentOriginExternalId;

    private final FastScaUpgradeGuidanceSuggestion shortTermSuggestion;

    private final FastScaUpgradeGuidanceSuggestion longTermSuggestion;

    @JsonCreator
    public FastScaUpgradeGuidance(@JsonProperty("componentId") UUID componentId,
            @JsonProperty("componentVersionId") UUID componentVersionId,
            @JsonProperty("componentOriginId") UUID componentOriginId,
            @JsonProperty("componentName") String componentName,
            @JsonProperty("componentVersionName") String componentVersionName,
            @JsonProperty("componentOriginExternalNamespace") String componentOriginExternalNamespace,
            @JsonProperty("componentOriginExternalId") String componentOriginExternalId,
            @JsonProperty("shortTermSuggestion") FastScaUpgradeGuidanceSuggestion shortTermSuggestion,
            @JsonProperty("longTermSuggestion") FastScaUpgradeGuidanceSuggestion longTermSuggestion) {
        this.componentId = componentId;
        this.componentVersionId = componentVersionId;
        this.componentOriginId = componentOriginId;
        this.componentName = componentName;
        this.componentVersionName = componentVersionName;
        this.componentOriginExternalNamespace = componentOriginExternalNamespace;
        this.componentOriginExternalId = componentOriginExternalId;
        this.shortTermSuggestion = shortTermSuggestion;
        this.longTermSuggestion = longTermSuggestion;
    }

    public FastScaUpgradeGuidance(UpgradeGuidance upgradeGuidance) {
    	Objects.requireNonNull(upgradeGuidance, "Upgrade guidance must be initialized.");
    	
    	this.componentId = upgradeGuidance.getComponentId().orElse(null);
    	this.componentVersionId = upgradeGuidance.getVersionId().orElse(null);
    	this.componentOriginId = upgradeGuidance.getVariantId().orElse(null);
    	this.componentName = upgradeGuidance.getComponentName();
    	this.componentVersionName = upgradeGuidance.getVersionName();
    	this.componentOriginExternalNamespace = upgradeGuidance.getVariantExternalNamespace().orElse(null);
    	this.componentOriginExternalId = upgradeGuidance.getVariantExternalId().orElse(null);
    	this.shortTermSuggestion = upgradeGuidance.getShortTermSuggestion().map(FastScaUpgradeGuidanceSuggestion::new).orElse(null);
    	this.longTermSuggestion = upgradeGuidance.getLongTermSuggestion().map(FastScaUpgradeGuidanceSuggestion::new).orElse(null);
    }
    
    /**
     * Gets the component id.
     * 
     * @return Returns the component id.
     */
    public UUID getComponentId() {
        return componentId;
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
     * Gets the component name.
     * 
     * @return Returns the component name.
     */
    public String getComponentName() {
        return componentName;
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
     * Gets the short term suggestion.
     * 
     * @return Returns the short term suggestion.
     */
    public Optional<FastScaUpgradeGuidanceSuggestion> getShortTermSuggestion() {
        return Optional.ofNullable(shortTermSuggestion);
    }

    /**
     * Gets the long term suggestion.
     * 
     * @return Returns the long term suggestion.
     */
    public Optional<FastScaUpgradeGuidanceSuggestion> getLongTermSuggestion() {
        return Optional.ofNullable(longTermSuggestion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponentId(), getComponentVersionId(), getComponentOriginId(), getComponentName(), getComponentVersionName(),
                getComponentOriginExternalNamespace(), getComponentOriginExternalId(), getShortTermSuggestion(), getLongTermSuggestion());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaUpgradeGuidance) {
            FastScaUpgradeGuidance otherFastScaUpgradeGuidance = (FastScaUpgradeGuidance) otherObject;

            return Objects.equals(getComponentId(), otherFastScaUpgradeGuidance.getComponentId())
                    && Objects.equals(getComponentVersionId(), otherFastScaUpgradeGuidance.getComponentVersionId())
                    && Objects.equals(getComponentOriginId(), otherFastScaUpgradeGuidance.getComponentOriginId())
                    && Objects.equals(getComponentName(), otherFastScaUpgradeGuidance.getComponentName())
                    && Objects.equals(getComponentVersionName(), otherFastScaUpgradeGuidance.getComponentVersionName())
                    && Objects.equals(getComponentOriginExternalNamespace(), otherFastScaUpgradeGuidance.getComponentOriginExternalNamespace())
                    && Objects.equals(getComponentOriginExternalId(), otherFastScaUpgradeGuidance.getComponentOriginExternalId())
                    && Objects.equals(getShortTermSuggestion(), otherFastScaUpgradeGuidance.getShortTermSuggestion())
                    && Objects.equals(getLongTermSuggestion(), otherFastScaUpgradeGuidance.getLongTermSuggestion());
        }

        return false;
    }
}
