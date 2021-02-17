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
package com.synopsys.integration.polaris.common.api;

import java.math.BigDecimal;

import com.google.gson.annotations.SerializedName;

public class PolarisResourcesPagination extends PolarisComponent {
    @SerializedName("offset")
    private BigDecimal offset;

    @SerializedName("limit")
    private BigDecimal limit;

    @SerializedName("total")
    private BigDecimal total;

    /**
     * The offset used for this request.  If null, no offset was applied.
     * @return offset
     */
    public BigDecimal getOffset() {
        return offset;
    }

    public void setOffset(final BigDecimal offset) {
        this.offset = offset;
    }

    /**
     * The maximum number of elements returned for this request.  If null, no limit was applied.
     * @return limit
     */
    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(final BigDecimal limit) {
        this.limit = limit;
    }

    /**
     * The total number of results matching the provided criteria, without regard to the provided offset or limit.
     * @return total
     */
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(final BigDecimal total) {
        this.total = total;
    }

}

