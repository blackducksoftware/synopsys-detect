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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.github.zafarkhaja.semver.Version;
import com.google.gson.Gson;

/**
 * Used for parsing the JSON response from the Nuget Registration API
 */
public class NugetApi3RegistrationJsonParser {
    final Gson gson;

    public NugetApi3RegistrationJsonParser(final Gson gson) {
        this.gson = gson;
    }

    public List<Version> findInspectionVersions(final String jsonResponse, final String inspectorName) {
        final NugetApi3Response nugetApi3Response = gson.fromJson(jsonResponse, NugetApi3Response.class);

        return getVersionsFromNugetResponse(nugetApi3Response, inspectorName);
    }

    private List<Version> getVersionsFromNugetResponse(final NugetApi3Response nugetApi3Response, final String inspectorName) {
        final List<Version> foundVersions = new ArrayList<>();

        for (final NugetApi3CatalogPage catalogPage : nugetApi3Response.getItems()) {
            for (final NugetApi3Package nugetApi3Package : catalogPage.getItems()) {
                final NugetApi3CatalogEntry catalogEntry = nugetApi3Package.getCatalogEntry();
                final Optional<Version> version = getVersionFromCatalogEntry(catalogEntry, inspectorName);

                version.ifPresent(foundVersions::add);
            }
        }

        return foundVersions;
    }

    private Optional<Version> getVersionFromCatalogEntry(final NugetApi3CatalogEntry catalogEntry, final String inspectorName) {
        Optional<Version> version = Optional.empty();
        if (isBlackDuckCatalogEntry(catalogEntry, inspectorName)) {
            final String foundVersion = catalogEntry.getPackageVersion();
            if (StringUtils.isNotBlank(foundVersion)) {
                version = Optional.of(Version.valueOf(foundVersion));
            }
        }

        return version;
    }

    private boolean isBlackDuckCatalogEntry(final NugetApi3CatalogEntry catalogEntry, final String inspectorName) {
        final boolean nameMatches = catalogEntry.getPackageName().equals(inspectorName);
        final boolean companyMatches = catalogEntry.getAuthors().equalsIgnoreCase("Black Duck Software") || catalogEntry.getAuthors().equalsIgnoreCase("Black Duck by Synopsys");

        return nameMatches && companyMatches;
    }
}
