/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.configuration.property;

import com.synopsys.integration.configuration.util.ProductMajorVersion;

//data class PropertyDeprecationInfo(val description: String, val failInVersion: ProductMajorVersion, val removeInVersion: ProductMajorVersion) {
//    fun getDeprecationText(): String = "$description It will cause failure in ${failInVersion.getDisplayValue()} and be removed in ${removeInVersion.getDisplayValue()}.";
//}
public class PropertyDeprecationInfo {
    private final String description;
    private final ProductMajorVersion failInVersion;
    private final ProductMajorVersion removeInVersion;

    public PropertyDeprecationInfo(final String description, final ProductMajorVersion failInVersion, final ProductMajorVersion removeInVersion) {
        this.description = description;
        this.failInVersion = failInVersion;
        this.removeInVersion = removeInVersion;
    }

    public String getDescription() {
        return description;
    }

    public ProductMajorVersion getFailInVersion() {
        return failInVersion;
    }

    public ProductMajorVersion getRemoveInVersion() {
        return removeInVersion;
    }

    public String getDeprecationText() {
        return getDescription() + " It will cause failure in " + getFailInVersion().getDisplayValue() + " and be removed in " + getRemoveInVersion().getDisplayValue() + ".";
    }
}
