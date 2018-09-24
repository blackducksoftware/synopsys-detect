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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget.api2;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.github.zafarkhaja.semver.Version;
import com.synopsys.integration.util.NameVersion;

public class NugetXmlParser {
    public List<Version> parseVersions(final Document xmlDocument, final String inspectorName) {
        final List<NugetEntry> nugetEntries = parseEntries(xmlDocument);

        return transformNugetEntries(nugetEntries, inspectorName);
    }

    private List<Version> transformNugetEntries(final List<NugetEntry> nugetEntries, final String inspectorName) {
        return nugetEntries.stream()
                   .filter((final NugetEntry nugetEntry) -> isBlackDuckPackage(nugetEntry, inspectorName))
                   .map(this::transformNugetEntry)
                   .collect(Collectors.toList());
    }

    private boolean isBlackDuckPackage(final NugetEntry nugetEntry, final String inspectorName) {
        final boolean nameMatches = nugetEntry.getName().equals(inspectorName);
        final boolean companyMatches = nugetEntry.getAuthors().contains("Black Duck Software") || nugetEntry.getAuthors().contains("Black Duck by Synopsys");
        final boolean oneAuthor = nugetEntry.getAuthors().size() == 1;

        return nameMatches && companyMatches && oneAuthor;
    }

    private Version transformNugetEntry(final NugetEntry nugetEntry) {
        return Version.valueOf(nugetEntry.getVersion());
    }

    private List<NugetEntry> parseEntries(final Document xmlDocument) {
        final List<NugetEntry> nugetEntries = new ArrayList<>();
        final NodeList nodeVersions = xmlDocument.getElementsByTagName("entry");

        for (int index = 0; index < nodeVersions.getLength(); index++) {
            final NodeList entryNodes = nodeVersions.item(index).getChildNodes();
            final NugetEntry nugetEntry = parseEntry(entryNodes);

            nugetEntries.add(nugetEntry);
        }

        return nugetEntries;
    }

    private NugetEntry parseEntry(final NodeList entryNodes) {
        String id = null;
        List<String> authors = null;
        NameVersion nameVersion = null;

        for (int index = 0; index < entryNodes.getLength(); index++) {
            final Node entryNode = entryNodes.item(index);

            if (entryNode == null || entryNode.getNodeName() == null) {
                continue;
            }

            if (entryNode.getNodeName().equalsIgnoreCase("id")) {
                id = entryNode.getTextContent();
            } else if (entryNode.getNodeName().equalsIgnoreCase("author")) {
                authors = getEntryAuthorNames(entryNode.getChildNodes());
            } else if (entryNode.getNodeName().equalsIgnoreCase("m:properties")) {
                final NodeList propertiesNodeList = entryNode.getChildNodes();
                nameVersion = parseEntryProperties(propertiesNodeList);
            }
        }

        return new NugetEntry(id, authors, nameVersion.getName(), nameVersion.getVersion());
    }

    private List<String> getEntryAuthorNames(final NodeList authorNameNodeList) {
        final List<String> authorNames = new ArrayList<>();

        for (int index = 0; index < authorNameNodeList.getLength(); index++) {
            final Node authorNode = authorNameNodeList.item(index);

            if (authorNode.getNodeName().equalsIgnoreCase("name")) {
                authorNames.add(authorNode.getTextContent());
            }
        }

        return authorNames;
    }

    private NameVersion parseEntryProperties(final NodeList propertiesNodeList) {
        final NameVersion nameVersion = new NameVersion();
        for (int index = 0; index < propertiesNodeList.getLength(); index++) {
            final Node propertyNode = propertiesNodeList.item(index);

            if (propertyNode.getNodeName().equalsIgnoreCase("d:id")) {
                nameVersion.setName(propertyNode.getTextContent());
            } else if (propertyNode.getNodeName().equalsIgnoreCase("d:version")) {
                nameVersion.setVersion(propertyNode.getTextContent());
            }
        }

        return nameVersion;
    }
}
