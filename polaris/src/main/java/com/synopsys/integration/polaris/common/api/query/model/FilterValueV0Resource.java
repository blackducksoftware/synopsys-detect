/*
 * polaris
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
package com.synopsys.integration.polaris.common.api.query.model;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisComponent;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class FilterValueV0Resource extends PolarisComponent {
    @SerializedName("data")
    private List<FilterValueV0> data = null;

    @SerializedName("meta")
    private PagedMetaV0 meta = null;

    public FilterValueV0Resource addDataItem(final FilterValueV0 dataItem) {
        if (this.data == null) {
            this.data = new ArrayList<>();
        }
        this.data.add(dataItem);
        return this;
    }

    /**
     * Get data
     * @return data
     */
    public List<FilterValueV0> getData() {
        return data;
    }

    public void setData(final List<FilterValueV0> data) {
        this.data = data;
    }

    /**
     * Get meta
     * @return meta
     */
    public PagedMetaV0 getMeta() {
        return meta;
    }

    public void setMeta(final PagedMetaV0 meta) {
        this.meta = meta;
    }

}

