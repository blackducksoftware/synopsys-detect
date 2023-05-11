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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

/**
 * fastSCA match.
 * 
 * Included in the fastSCA report for every identification.
 * 
 * @author skatzman
 */
public class FastScaMatch {
    private final FastScaMatchType matchType;

    private final List<String> dependencyTree;

    @JsonCreator
    public FastScaMatch(@JsonProperty("matchType") FastScaMatchType matchType,
            @JsonProperty("dependencyTree") List<String> dependencyTree) {
        this.matchType = matchType;
        this.dependencyTree = (dependencyTree != null) ? ImmutableList.copyOf(dependencyTree) : ImmutableList.of();
    }

    public FastScaMatchType getMatchType() {
        return matchType;
    }

    public List<String> getDependencyTree() {
        return dependencyTree;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMatchType(), getDependencyTree());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaMatch) {
            FastScaMatch otherFastScaMatch = (FastScaMatch) otherObject;

            return Objects.equals(getMatchType(), otherFastScaMatch.getMatchType())
                    && Objects.equals(getDependencyTree(), otherFastScaMatch.getDependencyTree());
        }

        return false;
    }
}
