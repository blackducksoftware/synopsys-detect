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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.apiversion2;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;

public class NugetApi2 {
    private final Logger logger = LoggerFactory.getLogger(NugetApi2.class);

    private final DocumentBuilder xmlDocumentBuilder;
    private final NugetApi2XmlParser nugetApi2XmlParser;

    public NugetApi2(final DocumentBuilder xmlDocumentBuilder, final NugetApi2XmlParser nugetApi2XmlParser) {
        this.xmlDocumentBuilder = xmlDocumentBuilder;
        this.nugetApi2XmlParser = nugetApi2XmlParser;
    }

    public List<Version> findVersions(final String nugetPackageRepo, final String inspectorName, UnauthenticatedRestConnection restConnection) {
        final Request request = new Request.Builder(nugetPackageRepo).addQueryParameter("id", "'" + inspectorName + "'").build();
        List<Version> foundVersions = new ArrayList<>();

        try {
            final Response response = restConnection.executeRequest(request);
            final InputStream inputStream = response.getContent();
            final Document xmlDocument = xmlDocumentBuilder.parse(inputStream);
            foundVersions = nugetApi2XmlParser.parseVersions(xmlDocument, inspectorName);
        } catch (final IOException | IntegrationException | SAXException e) {
            logger.warn(String.format("Failed to resolve nuget inspector (%s) version from url: %s", inspectorName, nugetPackageRepo));
            logger.debug(e.getMessage(), e);
        }

        return foundVersions;
    }
}
