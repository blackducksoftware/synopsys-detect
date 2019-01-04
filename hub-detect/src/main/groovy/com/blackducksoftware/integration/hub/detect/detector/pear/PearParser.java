/**
 * hub-detect
 *
 * Copyright (C) 2019 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.hub.detect.detector.pear;

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

import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.util.XmlUtil;
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PearParser {
    private final Logger logger = LoggerFactory.getLogger(PearParser.class);

    private final ExternalIdFactory externalIdFactory;
    private final DetectConfiguration detectConfiguration;

    public PearParser(final ExternalIdFactory externalIdFactory, final DetectConfiguration detectConfiguration) {
        this.externalIdFactory = externalIdFactory;
        this.detectConfiguration = detectConfiguration;
    }

    public PearParseResult parse(final File packageFile, final ExecutableOutput pearListing, final ExecutableOutput pearDependencies) throws ParserConfigurationException, SAXException, IOException {
        final PearParseResult result = new PearParseResult();
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        final Document packageXml = builder.parse(packageFile);
        final Node packageNode = XmlUtil.getNode("package", packageXml);
        result.name = XmlUtil.getNode("name", packageNode).getTextContent();
        final Node versionNode = XmlUtil.getNode("version", packageNode);
        final String version = XmlUtil.getNode("release", versionNode).getTextContent();
        result.version = version;
        result.dependencyGraph = parsePearDependencyList(pearListing, pearDependencies);
        return result;
    }

    public DependencyGraph parsePearDependencyList(final ExecutableOutput pearListing, final ExecutableOutput pearDependencies) {
        DependencyGraph graph = new MutableMapDependencyGraph();

        if (StringUtils.isNotBlank(pearDependencies.getErrorOutput()) || StringUtils.isNotBlank(pearListing.getErrorOutput())) {
            logger.error("There was an error during execution.");
            if (StringUtils.isNotBlank(pearListing.getErrorOutput())) {
                logger.error("Pear list error: ");
                logger.error(pearListing.getErrorOutput());
            }
            if (StringUtils.isNotBlank(pearDependencies.getErrorOutput())) {
                logger.error("Pear package-dependencies error: ");
                logger.error(pearDependencies.getErrorOutput());
            }
        } else if (!(pearDependencies.getStandardOutputAsList().size() > 0) || !(pearListing.getStandardOutputAsList().size() > 0)) {
            logger.error("No information retrieved from running pear commands");
        } else {
            final List<String> nameList = findDependencyNames(pearDependencies.getStandardOutputAsList());
            graph = createPearDependencyGraphFromList(pearListing.getStandardOutputAsList(), nameList);
        }

        return graph;
    }

    List<String> findDependencyNames(final List<String> content) {
        final List<String> nameList = new ArrayList<>();

        if (content.size() > 5) {
            final List<String> listing = content.subList(5, content.size() - 1);
            listing.forEach(line -> {
                final String[] dependencyInfo = splitIgnoringWhitespace(line, " ");

                final String dependencyName = dependencyInfo[2].trim();
                final String dependencyRequired = dependencyInfo[0].trim();

                if (StringUtils.isNotBlank(dependencyName)) {
                    if (!detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PEAR_ONLY_REQUIRED_DEPS, PropertyAuthority.None)) {
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

    DependencyGraph createPearDependencyGraphFromList(final List<String> dependencyList, final List<String> dependencyNames) {
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
