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

import java.util.Objects;
import java.util.Arrays;

import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.IOException;

// this file should not be edited - if changes are necessary, the generator should be updated, then this file should be re-created

public class Error extends PolarisComponent {
    @SerializedName("code")
    private String code;

    @SerializedName("title")
    private String title;

    @SerializedName("detail")
    private String detail;

    /**
     * The Pericles Code categorizing the problem.  Codes correspond 1-to-1 with Titles.
     * @return code
     */
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    /**
     * The Pericles Title (human readable) categorizing the problem.  Titles correspond 1-to-1 with Codes.
     * @return title
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * A human readable explanation of the problem that will vary depending on the exact request.  When present, this will describe the exact problem with the request (ie: field &#x60;abc&#x60; must be of type int, but string was found).
     * @return detail
     */
    public String getDetail() {
        return detail;
    }

    public void setDetail(final String detail) {
        this.detail = detail;
    }

}

