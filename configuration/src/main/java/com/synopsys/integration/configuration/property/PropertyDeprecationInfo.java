/**
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
