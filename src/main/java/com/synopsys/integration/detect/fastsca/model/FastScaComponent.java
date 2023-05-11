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
import com.synopsys.kb.httpclient.model.Component;
import com.synopsys.kb.httpclient.model.Link;

/**
 * fastSCA component.
 * 
 * Included in the fastSCA report for matches to components, component versions, and component origins.
 * 
 * @author skatzman
 */
public class FastScaComponent {
    private final UUID id;

    private final String name;

    private final String description;

    private final String homepage;

    private final String openHub;

    @JsonCreator
    public FastScaComponent(@JsonProperty("id") UUID id,
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("homepage") String homepage,
            @JsonProperty("openHub") String openHub) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.homepage = homepage;
        this.openHub = openHub;
    }

    public FastScaComponent(Component component) {
    	Objects.requireNonNull(component, "Component must be initialized.");
    	
    	this.id = component.getId();
    	this.name = component.getName();
    	this.description = component.getDescription();
    	this.homepage = component.getHomepageLink().map(Link::getHref).orElse(null);
    	this.openHub = component.getOpenHubLink().map(Link::getHref).orElse(null);
    }
    
    /**
     * Gets the component id.
     * 
     * @return Returns the component id.
     */
    public final UUID getId() {
        return id;
    }

    /**
     * Gets the component name.
     * 
     * @return Returns the component name.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the component description.
     * 
     * @return Returns the component description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the component's homepage URI.
     * 
     * @return Returns the component's homepage URI.
     */
    public Optional<String> getHomepage() {
        return Optional.ofNullable(homepage);
    }

    /**
     * Gets the component's OpenHub URI.
     * 
     * @return Returns the component's OpenHub URI.
     */
    public Optional<String> getOpenHub() {
        return Optional.ofNullable(openHub);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), getHomepage(), getOpenHub());
    }

    @Override
    public boolean equals(Object otherObject) {
        if (this == otherObject) {
            return true;
        } else if (otherObject instanceof FastScaComponent) {
            FastScaComponent otherFastScaComponent = (FastScaComponent) otherObject;

            return Objects.equals(getId(), otherFastScaComponent.getId())
                    && Objects.equals(getName(), otherFastScaComponent.getName())
                    && Objects.equals(getDescription(), otherFastScaComponent.getDescription())
                    && Objects.equals(getHomepage(), otherFastScaComponent.getHomepage())
                    && Objects.equals(getOpenHub(), otherFastScaComponent.getOpenHub());
        }

        return false;
    }
}
