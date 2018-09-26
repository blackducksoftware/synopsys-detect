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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.RestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;

public class NugetApi3 {
    private final Logger logger = LoggerFactory.getLogger(NugetApi3.class);

    private final NugetApi3RegistrationJsonParser nugetApi3RegistrationJsonParser;
    private final NugetApi3IndexJsonParser nugetApi3IndexJsonParser;

    public NugetApi3(final NugetApi3RegistrationJsonParser nugetApi3RegistrationJsonParser, final NugetApi3IndexJsonParser nugetApi3IndexJsonParser) {
        this.nugetApi3RegistrationJsonParser = nugetApi3RegistrationJsonParser;
        this.nugetApi3IndexJsonParser = nugetApi3IndexJsonParser;
    }

    public List<Version> findVersions(final String nugetPackageRepo, final String inspectorName, RestConnection restConnection) {
        final List<Version> foundVersions = new ArrayList<>();
        Request request = null;

        try {
            final Optional<String> baseUrl = fetchRegistrationBaseUrl(nugetPackageRepo, restConnection);
            if (baseUrl.isPresent()) {
                String url = baseUrl.get();
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                String newPath = url + "/" + inspectorName.toLowerCase() + "/index.json";
                request = new Request.Builder(newPath).build();
            } else {
                throw new IntegrationException(String.format("Base URL could not be discovered from [%s]", nugetPackageRepo));
            }
        } catch (final IntegrationException e) {
            logger.warn(String.format("Failed to build uri %s/%s/index.json", nugetPackageRepo, inspectorName));
            logger.debug(e.getMessage(), e);
        }

        try {
            final Response response = restConnection.executeRequest(request);
            final String jsonResponse = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
            final List<Version> versions = nugetApi3RegistrationJsonParser.findInspectionVersions(jsonResponse, inspectorName);

            foundVersions.addAll(versions);
        } catch (final IOException | IntegrationException e) {
            logger.warn(String.format("Failed to resolve nuget inspector (%s) version from url: %s", inspectorName, nugetPackageRepo));
            logger.debug(e.getMessage(), e);
        }

        return foundVersions;
    }

    /**
     * Fetches the base url for the Registration api from the Nuget V3 API index
     */
    private Optional<String> fetchRegistrationBaseUrl(final String nugetPackageRepo, RestConnection restConnection) {
        Optional<String> registrationUrl = Optional.empty();
        try {
            final Request request = new Request.Builder(nugetPackageRepo).build();
            final Response response = restConnection.executeRequest(request);
            final String indexJson = IOUtils.toString(response.getContent(), StandardCharsets.UTF_8);
            final Optional<NugetApi3Resource> registrationBaseUrlResource = nugetApi3IndexJsonParser.parseResourceFromIndexJson(indexJson, NugetApi3ResourceType.RegistrationBaseUrl);

            if (registrationBaseUrlResource.isPresent()) {
                registrationUrl = registrationBaseUrlResource.get().getId();
            }
        } catch (final IOException | IntegrationException e) {
            logger.warn(String.format("Failed to find RegistrationBaseUrl in: %s", nugetPackageRepo));
            logger.debug(e.getMessage(), e);
        }

        return registrationUrl;
    }
}
