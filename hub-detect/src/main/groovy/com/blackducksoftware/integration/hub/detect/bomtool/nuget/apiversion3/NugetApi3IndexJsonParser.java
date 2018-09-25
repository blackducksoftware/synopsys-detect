/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion3;

import java.util.Optional;

import com.google.gson.Gson;

public class NugetApi3IndexJsonParser {
    final Gson gson;

    public NugetApi3IndexJsonParser(final Gson gson) {
        this.gson = gson;
    }

    public Optional<NugetApi3Resource> parseResourceFromIndexJson(final String indexJson, final NugetApi3ResourceType nugetApi3ResourceType) {
        return parseResourceFromIndexJson(indexJson, nugetApi3ResourceType.getType());
    }

    private Optional<NugetApi3Resource> parseResourceFromIndexJson(final String indexJson, final String resourceType) {
        final NugetApi3Index nugetApi3Index = gson.fromJson(indexJson, NugetApi3Index.class);
        final Optional<NugetApi3Resource> resource = resourceFromIndex(nugetApi3Index, resourceType);

        return resource;
    }

    private Optional<NugetApi3Resource> resourceFromIndex(final NugetApi3Index nugetApi3Index, final String resourceType) {
        final Optional<NugetApi3Resource> nugetResource = nugetApi3Index.getResources().stream()
                                                              .filter(p -> p.getType().equals(resourceType))
                                                              .findFirst();

        return nugetResource;
    }
}
