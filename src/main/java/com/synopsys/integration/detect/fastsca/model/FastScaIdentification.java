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
import java.util.Optional;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * fastSCA identification.
 * 
 * Included in the fastSCA report for an one or more matches to a component, component version, or component origin.
 * 
 * @author skatzman
 */
@JsonPropertyOrder(value = { "component", "componentVersion", "componentOrigin", "upgradeGuidance", "vulnerabilities", "matches" })
public class FastScaIdentification {
    private final FastScaComponent component;

    private final FastScaComponentVersion componentVersion;

    private final FastScaComponentOrigin componentOrigin;

    private final FastScaUpgradeGuidance upgradeGuidance;

    private final List<FastScaVulnerability> vulnerabilities;

    private final Set<FastScaMatch> matches;

    @JsonCreator
    public FastScaIdentification(@JsonProperty("component") FastScaComponent component,
            @JsonProperty("componentVersion") FastScaComponentVersion componentVersion,
            @JsonProperty("componentOrigin") FastScaComponentOrigin componentOrigin,
            @JsonProperty("upgradeGuidance") FastScaUpgradeGuidance upgradeGuidance,
            @JsonProperty("vulnerabilities") Collection<FastScaVulnerability> vulnerabilities,
            @JsonProperty("matches") Collection<FastScaMatch> matches) {
        this.component = component;
        this.componentVersion = componentVersion;
        this.componentOrigin = componentOrigin;
        this.upgradeGuidance = upgradeGuidance;
        this.vulnerabilities = (vulnerabilities != null) ? ImmutableList.copyOf(vulnerabilities) : ImmutableList.of();
        this.matches = (matches != null) ? ImmutableSet.copyOf(matches) : ImmutableSet.of();
    }

    /**
     * Gets the component.
     * 
     * @return Returns the component.
     */
    public FastScaComponent getComponent() {
        return component;
    }

    /**
     * Gets the component version.
     * 
     * @return Returns the component version.
     */
    public Optional<FastScaComponentVersion> getComponentVersion() {
        return Optional.ofNullable(componentVersion);
    }

    /**
     * Gets the component origin.
     * 
     * @return Returns the component origin.
     */
    public Optional<FastScaComponentOrigin> getComponentOrigin() {
        return Optional.ofNullable(componentOrigin);
    }

    /**
     * Gets the vulnerabilities.
     * 
     * @return Returns the vulnerabilities.
     */
    public List<FastScaVulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    /**
     * Gets the upgrade guidance.
     * 
     * @return Returns the upgrade guidance.
     */
    public Optional<FastScaUpgradeGuidance> getUpgradeGuidance() {
        return Optional.ofNullable(upgradeGuidance);
    }

    /**
     * Gets the matches.
     * 
     * @return Returns the matches.
     */
    public Set<FastScaMatch> getMatches() {
        return matches;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getComponent(), getComponentVersion(), getComponentOrigin(), getVulnerabilities(), getUpgradeGuidance(), getMatches());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaIdentification) {
            FastScaIdentification otherFastScaIdentification = (FastScaIdentification) otherObject;

            return Objects.equals(getComponent(), otherFastScaIdentification.getComponent())
                    && Objects.equals(getComponentVersion(), otherFastScaIdentification.getComponentVersion())
                    && Objects.equals(getComponentOrigin(), otherFastScaIdentification.getComponentOrigin())
                    && Objects.equals(getVulnerabilities(), otherFastScaIdentification.getVulnerabilities())
                    && Objects.equals(getUpgradeGuidance(), otherFastScaIdentification.getUpgradeGuidance())
                    && Objects.equals(getMatches(), otherFastScaIdentification.getMatches());
        }

        return false;
    }
}
