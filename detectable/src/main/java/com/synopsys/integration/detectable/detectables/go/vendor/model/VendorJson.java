/**
 * detectable
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
package com.synopsys.integration.detectable.detectables.go.vendor.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.synopsys.integration.util.Stringable;

public class VendorJson extends Stringable {
    private final String comment;
    private final String ignore;
    @SerializedName("package")
    private final List<PackageData> packages;
    private final String rootPath;

    public VendorJson(final String comment, final String ignore, final List<PackageData> packages, final String rootPath) {
        this.comment = comment;
        this.ignore = ignore;
        this.packages = packages;
        this.rootPath = rootPath;
    }

    public String getComment() {
        return comment;
    }

    public String getIgnore() {
        return ignore;
    }

    public List<PackageData> getPackages() {
        return packages;
    }

    public String getRootPath() {
        return rootPath;
    }
}
