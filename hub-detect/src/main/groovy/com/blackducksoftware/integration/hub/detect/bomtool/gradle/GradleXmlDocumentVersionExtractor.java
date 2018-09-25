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
package com.blackducksoftware.integration.hub.detect.bomtool.gradle;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.github.zafarkhaja.semver.Version;

public class GradleXmlDocumentVersionExtractor {
    public Optional<Version> detectVersionFromXML(final Document xmlDocument, final String versionRange) {
        final List<Version> foundVersions = new ArrayList<>();
        final NodeList nodeVersions = xmlDocument.getElementsByTagName("version");
        for (int i = 0; i < nodeVersions.getLength(); i++) {
            final String versionNodeText = nodeVersions.item(i).getTextContent();
            final Version foundVersion = Version.valueOf(versionNodeText);
            foundVersions.add(foundVersion);
        }

        Version bestVersion = null;
        for (final Version foundVersion : foundVersions) {
            if ((bestVersion == null || foundVersion.greaterThan(bestVersion)) && foundVersion.satisfies(versionRange)) {
                bestVersion = foundVersion;
            }
        }

        return Optional.ofNullable(bestVersion);
    }
}
