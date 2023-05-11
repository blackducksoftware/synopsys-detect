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

/**
 * fastSCA metadata.
 * 
 * Included in the fastSCA report to give context to the scan execution that generated a given report.
 * 
 * @author skatzman
 */
public class FastScaMetadata {
    private final UUID id;

    private final String toolName;

    private final String toolVersion;

    private final OffsetDateTime executedAt;

    private final String projectName;

    private final String projectVersionName;

    @JsonCreator
    public FastScaMetadata(@JsonProperty("id") UUID id,
            @JsonProperty("toolName") String toolName,
            @JsonProperty("toolVersion") String toolVersion,
            @JsonProperty("executedAt") OffsetDateTime executedAt,
            @JsonProperty("projectName") String projectName,
            @JsonProperty("projectVersionName") String projectVersionName) {
        this.id = id;
        this.toolName = toolName;
        this.toolVersion = toolVersion;
        this.executedAt = executedAt;
        this.projectName = projectName;
        this.projectVersionName = projectVersionName;
    }

    /**
     * Gets the identifier that uniquely represents this scan.
     * 
     * @return Returns the id.
     */
    public UUID getId() {
        return id;
    }

    /**
     * Gets the name of the tool that managed this scan.
     * 
     * @return Returns the tool name.
     */
    public String getToolName() {
        return toolName;
    }

    /**
     * Gets the version of the tool that managed this scan.
     * 
     * @return Returns the tool version.
     */
    public String getToolVersion() {
        return toolVersion;
    }

    /**
     * Gets the execution timestamp for this scan.
     * 
     * @return Returns the execution timestamp.
     */
    public OffsetDateTime getExecutedAt() {
        return executedAt;
    }

    /**
     * Gets the project name that is associated to this scan.
     * 
     * @return Returns the project name.
     */
    public Optional<String> getProjectName() {
        return Optional.ofNullable(projectName);
    }

    /**
     * Gets the project version name that is associated to this scan.
     * 
     * @return Returns the project version name.
     */
    public Optional<String> getProjectVersionName() {
        return Optional.ofNullable(projectVersionName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getToolName(), getToolVersion(), getExecutedAt(), getProjectName(), getProjectVersionName());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaMetadata) {
            FastScaMetadata otherFastScaMetadata = (FastScaMetadata) otherObject;

            return Objects.equals(getId(), otherFastScaMetadata.getId())
                    && Objects.equals(getToolName(), otherFastScaMetadata.getToolName())
                    && Objects.equals(getToolVersion(), otherFastScaMetadata.getToolVersion())
                    && Objects.equals(getExecutedAt(), otherFastScaMetadata.getExecutedAt())
                    && Objects.equals(getProjectName(), otherFastScaMetadata.getProjectName())
                    && Objects.equals(getProjectVersionName(), otherFastScaMetadata.getProjectVersionName());
        }

        return false;
    }
}
