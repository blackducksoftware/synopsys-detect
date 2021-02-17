/*
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
package com.synopsys.integration.detectable.detectables.npm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.Discovery;
import com.synopsys.integration.detectable.detectables.npm.packagejson.model.PackageJson;

public class NpmPackageJsonDiscoverer {
    private final Gson gson;

    public NpmPackageJsonDiscoverer(final Gson gson) {
        this.gson = gson;
    }

    public Discovery discover(final File packageJson) {
        try {
            final String packageJsonText = FileUtils.readFileToString(packageJson, StandardCharsets.UTF_8);
            final PackageJson packageJsonModel = gson.fromJson(packageJsonText, PackageJson.class);
            return new Discovery.Builder().success(packageJsonModel.name, packageJsonModel.version).build();
        } catch (final IOException e) {
            return new Discovery.Builder().exception(e).build();
        }
    }
}
