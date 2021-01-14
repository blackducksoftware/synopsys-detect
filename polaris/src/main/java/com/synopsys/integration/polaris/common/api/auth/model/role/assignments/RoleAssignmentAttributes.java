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
package com.synopsys.integration.polaris.common.api.auth.model.role.assignments;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.polaris.common.api.PolarisAttributes;

public class RoleAssignmentAttributes extends PolarisAttributes {
    @SerializedName("expires-by")
    private String expiresBy;

    @SerializedName("object")
    private String object;

    /**
     * Get expiresBy
     * @return expiresBy
     */
    public String getExpiresBy() {
        return expiresBy;
    }

    public void setExpiresBy(final String expiresBy) {
        this.expiresBy = expiresBy;
    }

    /**
     * Get object
     * @return object
     */
    public String getObject() {
        return object;
    }

    public void setObject(final String object) {
        this.object = object;
    }

}

