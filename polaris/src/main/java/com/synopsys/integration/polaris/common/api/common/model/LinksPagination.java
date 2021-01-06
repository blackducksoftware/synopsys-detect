/**
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
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class LinksPagination extends PolarisComponent {
    @SerializedName("next")
    private String next;

    @SerializedName("prev")
    private String prev;

    @SerializedName("first")
    private String first;

    @SerializedName("last")
    private String last;

    /**
     * An absolute URL to the next set of results.  May be null if no next set is available.
     * @return next
     */
    public String getNext() {
        return next;
    }

    public void setNext(final String next) {
        this.next = next;
    }

    /**
     * An absolute URL to the previous set of results.  May be null if no previous set is available.
     * @return prev
     */
    public String getPrev() {
        return prev;
    }

    public void setPrev(final String prev) {
        this.prev = prev;
    }

    /**
     * An absolute URL to the first set of results.  May be null if no first set is available.
     * @return first
     */
    public String getFirst() {
        return first;
    }

    public void setFirst(final String first) {
        this.first = first;
    }

    /**
     * An absolute URL to the final set of results.  May be null if no final set is available.
     * @return last
     */
    public String getLast() {
        return last;
    }

    public void setLast(final String last) {
        this.last = last;
    }

}

