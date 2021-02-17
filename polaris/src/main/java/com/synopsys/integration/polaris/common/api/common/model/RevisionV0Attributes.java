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
package com.synopsys.integration.polaris.common.api.common.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class RevisionV0Attributes extends PolarisComponent {
    @SerializedName("name")
    private String name;

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("modified-off-the-record")
    private Boolean modifiedOffTheRecord = false;

    /**
     * &#x60;Mutable&#x60;
     * @return name
     */
    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The time when the revision is created.
     * @return timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final String timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * A Revision is &#x60;modified-off-the-record&#x60; when it doesn&#39;t exist in the Scm.  Most Revisions come directly from the Scm, hence they aren&#39;t &#x60;modified-off-the-record&#x60;.
     * @return modifiedOffTheRecord
     */
    public Boolean getModifiedOffTheRecord() {
        return modifiedOffTheRecord;
    }

    public void setModifiedOffTheRecord(final Boolean modifiedOffTheRecord) {
        this.modifiedOffTheRecord = modifiedOffTheRecord;
    }

}

