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

import java.util.HashMap;
import java.util.Map;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class QueryParams extends PolarisComponent {
    @SerializedName("filters")
    private TypedParamsFilterParams filters = null;

    @SerializedName("sorting")
    private TypedParamsSortingParams sorting = null;

    @SerializedName("grouping")
    private TypedParamsGroupingParams grouping = null;

    @SerializedName("includedFields")
    private TypedParamsIncludedFieldsParams includedFields = null;

    @SerializedName("includedRelations")
    private TypedParamsIncludedRelationsParams includedRelations = null;

    @SerializedName("pagination")
    private Map<String, Integer> pagination = null;

    /**
     * Get filters
     * @return filters
     */
    public TypedParamsFilterParams getFilters() {
        return filters;
    }

    public void setFilters(final TypedParamsFilterParams filters) {
        this.filters = filters;
    }

    /**
     * Get sorting
     * @return sorting
     */
    public TypedParamsSortingParams getSorting() {
        return sorting;
    }

    public void setSorting(final TypedParamsSortingParams sorting) {
        this.sorting = sorting;
    }

    /**
     * Get grouping
     * @return grouping
     */
    public TypedParamsGroupingParams getGrouping() {
        return grouping;
    }

    public void setGrouping(final TypedParamsGroupingParams grouping) {
        this.grouping = grouping;
    }

    /**
     * Get includedFields
     * @return includedFields
     */
    public TypedParamsIncludedFieldsParams getIncludedFields() {
        return includedFields;
    }

    public void setIncludedFields(final TypedParamsIncludedFieldsParams includedFields) {
        this.includedFields = includedFields;
    }

    /**
     * Get includedRelations
     * @return includedRelations
     */
    public TypedParamsIncludedRelationsParams getIncludedRelations() {
        return includedRelations;
    }

    public void setIncludedRelations(final TypedParamsIncludedRelationsParams includedRelations) {
        this.includedRelations = includedRelations;
    }

    public QueryParams putPaginationItem(final String key, final Integer paginationItem) {
        if (this.pagination == null) {
            this.pagination = new HashMap<>();
        }
        this.pagination.put(key, paginationItem);
        return this;
    }

    /**
     * Get pagination
     * @return pagination
     */
    public Map<String, Integer> getPagination() {
        return pagination;
    }

    public void setPagination(final Map<String, Integer> pagination) {
        this.pagination = pagination;
    }

}

