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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api3;

import java.util.Optional;

import com.google.gson.Gson;

public class NugetIndexJsonParser {
    final Gson gson;

    public NugetIndexJsonParser(final Gson gson) {
        this.gson = gson;
    }

    public Optional<NugetResource> parseResourceFromIndexJson(final String indexJson, final ResourceType resourceType) {
        return parseResourceFromIndexJson(indexJson, resourceType.getType());
    }

    private Optional<NugetResource> parseResourceFromIndexJson(final String indexJson, final String resourceType) {
        final NugetIndex nugetIndex = gson.fromJson(indexJson, NugetIndex.class);
        final Optional<NugetResource> resource = resourceFromIndex(nugetIndex, resourceType);

        return resource;
    }

    private Optional<NugetResource> resourceFromIndex(final NugetIndex nugetIndex, final String resourceType) {
        final Optional<NugetResource> nugetResource = nugetIndex.getResources().stream()
                                                          .filter(p -> p.getType().equals(resourceType))
                                                          .findFirst();

        return nugetResource;
    }
}
