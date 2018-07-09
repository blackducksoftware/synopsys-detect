/*
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
package com.blackducksoftware.integration.hub.detect.bomtool.pear;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.MutableMapDependencyGraph;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfigWrapper;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.util.XmlUtil;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;

import groovy.transform.TypeChecked;

public class PearDependencyFinder {
    private final Logger logger = LoggerFactory.getLogger(PearDependencyFinder.class);

    private final ExternalIdFactory externalIdFactory;
    private final DetectConfigWrapper detectConfigWrapper;

    public PearDependencyFinder(final ExternalIdFactory externalIdFactory, final DetectConfigWrapper detectConfigWrapper) {
        this.externalIdFactory = externalIdFactory;
        this.detectConfigWrapper = detectConfigWrapper;
    }

    public PearParseResult parse(final File packageFile, final ExecutableOutput pearListing, final ExecutableOutput pearDependencies) throws ParserConfigurationException, SAXException, IOException {
        final PearParseResult result = new PearParseResult();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Document packageXml = builder.parse(packageFile);
        result.name = XmlUtil.getNode("name", packageXml).getTextContent();
        final Node versionNode = XmlUtil.getNode("version", packageXml);
        final String version = XmlUtil.getNode("release", versionNode).getTextContent();
        result.version = version;
        result.dependencyGraph = parsePearDependencyList(pearListing, pearDependencies);
        return result;
    }

    @TypeChecked
    public DependencyGraph parsePearDependencyList(final ExecutableOutput pearListing, final ExecutableOutput pearDependencies) {
        DependencyGraph graph = new MutableMapDependencyGraph();

        if (pearDependencies.getErrorOutputAsList().size() > 0 || pearListing.getErrorOutputAsList().size() > 0) {
            logger.error("There was an error during execution.");
        } else if (!(pearDependencies.getStandardOutputAsList().size() > 0) || !(pearListing.getStandardOutputAsList().size() > 0)) {
            logger.error("No information retrieved from running pear commands");
        } else {
            final List<String> nameList = findDependencyNames(pearDependencies.getStandardOutputAsList());
            graph = createPearDependencyGraphFromList(pearListing.getStandardOutputAsList(), nameList);
        }

        return graph;
    }

    @TypeChecked
    private List<String> findDependencyNames(final List<String> content) {
        final List<String> nameList = new ArrayList<>();

        if (content.size() > 5) {
            final List<String> listing = content.subList(5, content.size() - 1);
            listing.forEach(line -> {
                final String[] dependencyInfo = splitIgnoringWhitespace(line, " ");

                final String dependencyName = dependencyInfo[2].trim();
                final String dependencyRequired = dependencyInfo[0].trim();

                if (StringUtils.isNotBlank(dependencyName)) {
                    if (!detectConfigWrapper.getBooleanProperty(DetectProperty.DETECT_PEAR_ONLY_REQUIRED_DEPS)) {
                        nameList.add(last(dependencyName.split("/")));
                    } else {
                        if (BooleanUtils.toBoolean(dependencyRequired)) {
                            nameList.add(last(dependencyName.split("/")));
                        }
                    }
                }
            });
        }

        return nameList;

    }

    private String[] splitIgnoringWhitespace(final String theString, final String seperator) {
        final String[] rawPieces = theString.trim().split(seperator);
        final String[] actualPieces = new String[rawPieces.length];
        int cnt = 0;
        for (final String piece : rawPieces) {
            if (StringUtils.isNotBlank(piece)) {
                actualPieces[cnt] = piece;
                cnt++;
            }
        }
        return actualPieces;
    }

    private String last(final String[] array) {
        return array[array.length - 1];
    }

    @TypeChecked
    private DependencyGraph createPearDependencyGraphFromList(final List<String> dependencyList, final List<String> dependencyNames) {
        final MutableDependencyGraph graph = new MutableMapDependencyGraph();

        if (dependencyList.size() > 3) {
            final List<String> listing = dependencyList.subList(3, dependencyList.size() - 1);
            listing.forEach(line -> {
                final String[] dependencyInfo = splitIgnoringWhitespace(line, " ");

                final String packageName = dependencyInfo[0].trim();
                final String packageVersion = dependencyInfo[1].trim();

                if (dependencyInfo.length > 0 && dependencyNames.contains(packageName)) {
                    final Dependency child = new Dependency(packageName, packageVersion, externalIdFactory.createNameVersionExternalId(Forge.PEAR, packageName, packageVersion));

                    graph.addChildToRoot(child);
                }
            });
        }

        return graph;
    }
}
