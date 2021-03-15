/**
 * polaris
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Link extends PolarisComponent {
    @SerializedName("uriBuilder")
    private UriBuilder uriBuilder = null;

    @SerializedName("rel")
    private String rel;

    @SerializedName("rels")
    private List<String> rels = null;

    @SerializedName("params")
    private Map<String, String> params = null;

    @SerializedName("title")
    private String title;

    @SerializedName("uri")
    private String uri;

    @SerializedName("type")
    private String type;

    /**
     * Get uriBuilder
     * @return uriBuilder
     */
    public UriBuilder getUriBuilder() {
        return uriBuilder;
    }

    public void setUriBuilder(final UriBuilder uriBuilder) {
        this.uriBuilder = uriBuilder;
    }

    /**
     * Get rel
     * @return rel
     */
    public String getRel() {
        return rel;
    }

    public void setRel(final String rel) {
        this.rel = rel;
    }

    public Link addRelsItem(final String relsItem) {
        if (this.rels == null) {
            this.rels = new ArrayList<>();
        }
        this.rels.add(relsItem);
        return this;
    }

    /**
     * Get rels
     * @return rels
     */
    public List<String> getRels() {
        return rels;
    }

    public void setRels(final List<String> rels) {
        this.rels = rels;
    }

    public Link putParamsItem(final String key, final String paramsItem) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.put(key, paramsItem);
        return this;
    }

    /**
     * Get params
     * @return params
     */
    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(final Map<String, String> params) {
        this.params = params;
    }

    /**
     * Get title
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Get uri
     * @return uri
     */
    public String getUri() {
        return uri;
    }

    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * Get type
     * @return type
     */
    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

}

