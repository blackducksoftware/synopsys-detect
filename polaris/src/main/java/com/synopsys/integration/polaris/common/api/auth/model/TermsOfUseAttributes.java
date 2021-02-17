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
package com.synopsys.integration.polaris.common.api.auth.model;

import com.synopsys.integration.polaris.common.api.PolarisComponent;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class TermsOfUseAttributes extends PolarisComponent {
    @SerializedName("sha256")
    private List<byte[]> sha256 = new ArrayList<>();

    @SerializedName("text")
    private String text;

    public TermsOfUseAttributes addSha256Item(final byte[] sha256Item) {
        this.sha256.add(sha256Item);
        return this;
    }

    /**
     * Get sha256
     * @return sha256
     */
    public List<byte[]> getSha256() {
        return sha256;
    }

    public void setSha256(final List<byte[]> sha256) {
        this.sha256 = sha256;
    }

    /**
     * Get text
     * @return text
     */
    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}

