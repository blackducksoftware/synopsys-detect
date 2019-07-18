/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.maven.parsing.parse;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class PomDocumentParser {
    private ExternalIdFactory externalIdFactory;
    private final DocumentBuilder documentBuilder;

    public PomDocumentParser(final ExternalIdFactory externalIdFactory, DocumentBuilder documentBuilder) {
        this.externalIdFactory = externalIdFactory;
        this.documentBuilder = documentBuilder;
    }

    public List<Dependency> parse(File pomFile, boolean includePlugins) throws IOException, SAXException {
        Document doc = documentBuilder.parse(pomFile);

        //Dependencies
        List<Dependency> dependencyNodes = toElementStream(doc.getElementsByTagName("dependencies"))
                                               .map(element -> element.getElementsByTagName("dependency"))
                                               .flatMap(this::toElementStream)
                                               .map(this::dependencyFromElement)
                                               .collect(Collectors.toList());

        //Plugins
        List<Dependency> pluginsNodes = toElementStream(doc.getElementsByTagName("plugins"))
                                            .map(element -> element.getElementsByTagName("plugin"))
                                            .flatMap(this::toElementStream)
                                            .map(this::dependencyFromElement)
                                            .collect(Collectors.toList());

        List<Dependency> allDependencies = new ArrayList<>();
        allDependencies.addAll(dependencyNodes);
        if (includePlugins) {
            allDependencies.addAll(pluginsNodes);
        }
        return allDependencies;
    }

    Dependency dependencyFromElement(Element element) {
        String group = contentFromElementNamed(element, "groupId");
        String artifact = contentFromElementNamed(element, "artifactId");
        String version = contentFromElementNamed(element, "version");

        final ExternalId externalId = externalIdFactory.createMavenExternalId(group, artifact, version);
        return new Dependency(artifact, version, externalId);

    }

    String contentFromElementNamed(Element parent, String name) {
        NodeList nodes = parent.getElementsByTagName(name);
        if (nodes.getLength() > 0) {
            return nodes.item(0).getTextContent();
        } else {
            return "";
        }
    }

    Stream<Element> toElementStream(NodeList nodelist) {
        List<Element> nodes = new ArrayList<>();
        for (int i = 0; i < nodelist.getLength(); i++) {
            nodeToElement(nodelist.item(i)).ifPresent(nodes::add);
        }
        return nodes.stream();
    }

    Optional<Element> nodeToElement(Node node) {
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            return Optional.of((Element) node);
        } else {
            return Optional.empty();
        }
    }
}
