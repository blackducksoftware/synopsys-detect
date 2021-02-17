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

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class PagedQueryMetaV0 extends PagedMetaV0 {
    @SerializedName("group-by")
    private String groupBy;

    @SerializedName("complete")
    private Boolean complete;

    @SerializedName("run-count")
    private Integer runCount;

    /**
     * The string describing the relationship that the result is grouped-by.
     * @return groupBy
     */
    public String getGroupBy() {
        return groupBy;
    }

    public void setGroupBy(final String groupBy) {
        this.groupBy = groupBy;
    }

    /**
     * Whether the response contained complete run information or not.  When the response is not complete, it should generally be retried at a later time.
     * @return complete
     */
    public Boolean getComplete() {
        return complete;
    }

    public void setComplete(final Boolean complete) {
        this.complete = complete;
    }

    /**
     * The number of runs resolved based off the query.
     * @return runCount
     */
    public Integer getRunCount() {
        return runCount;
    }

    public void setRunCount(final Integer runCount) {
        this.runCount = runCount;
    }

}

