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
package com.blackducksoftware.integration.hub.detect.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.detect.configuration.ConnectionManager;
import com.blackducksoftware.integration.hub.detect.exception.DetectUserFriendlyException;
import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.connection.UnauthenticatedRestConnection;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.request.Response;
import com.synopsys.integration.util.ResourceUtil;

public class MavenMetadataService {
    private final Logger logger = LoggerFactory.getLogger(MavenMetadataService.class);

    private final DocumentBuilder xmlDocumentBuilder;
    private final ConnectionManager connectionManager;

    public MavenMetadataService(final DocumentBuilder xmlDocumentBuilder, final ConnectionManager connectionManager) {
        this.xmlDocumentBuilder = xmlDocumentBuilder;
        this.connectionManager = connectionManager;
    }

    public Document fetchXmlDocumentFromFile(final File mavenMetadataXmlFile) throws IOException, SAXException {
        final InputStream inputStream = new FileInputStream(mavenMetadataXmlFile);
        final Document xmlDocument = xmlDocumentBuilder.parse(inputStream);

        return xmlDocument;
    }

    public Document fetchXmlDocumentFromUrl(final String mavenMetadataUrl) throws IntegrationException, IOException, SAXException, DetectUserFriendlyException {
        final Request request = new Request.Builder().uri(mavenMetadataUrl).build();
        Document xmlDocument = null;
        Response response = null;

        try (final UnauthenticatedRestConnection restConnection = connectionManager.createUnauthenticatedRestConnection(mavenMetadataUrl)) {
            response = restConnection.executeRequest(request);
            final InputStream inputStream = response.getContent();
            xmlDocument = xmlDocumentBuilder.parse(inputStream);
        } finally {
            ResourceUtil.closeQuietly(response);
        }

        return xmlDocument;
    }

    public Optional<String> parseVersionFromXML(final Document xmlDocument, final String versionRange) {
        final List<String> foundVersions = new ArrayList<>();
        final NodeList nodeVersions = xmlDocument.getElementsByTagName("version");
        for (int i = 0; i < nodeVersions.getLength(); i++) {
            final String versionNodeText = nodeVersions.item(i).getTextContent();
            foundVersions.add(versionNodeText);
        }

        return getBestVersion(foundVersions, versionRange);
    }

    public String fetchBestVersionForUrl(String url, String versionRange) throws IOException, DetectUserFriendlyException, SAXException, IntegrationException {
        final Document xmlDocument = fetchXmlDocumentFromUrl(url);
        final Optional<String> version = parseVersionFromXML(xmlDocument, versionRange);

        return version.orElse(versionRange);
    }

    public Optional<String> getBestVersion(final List<String> versions, final String versionRange) {
        final Optional<String> bestVersion = versions.stream()
                                                 .map(Version::valueOf)
                                                 .filter(p -> p.satisfies(versionRange))
                                                 .max(Version::compareTo)
                                                 .map(Version::toString);

        return bestVersion;
    }
}
