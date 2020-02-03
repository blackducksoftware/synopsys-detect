/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.maven.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.SAXParserFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseExtractor;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseOptions;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class MavenParseExtractorTest {
    private final Set<String> expectedDependencies = new HashSet<>();
    private final Set<String> pluginDependencies = new HashSet<>();

    @BeforeEach
    public void setup() {
        expectedDependencies.add("commons-io:commons-io:2.6");
        expectedDependencies.add("javax.servlet:servlet-api:2.5");
        expectedDependencies.add("org.jetbrains.teamcity:agent-api:${teamcity.version}");
        expectedDependencies.add("org.apache.commons:commons-lang3:3.7");
        expectedDependencies.add("org.jdom:jdom:2.0.2");
        expectedDependencies.add("com.blackducksoftware.integration:hub-common:${hub.common.version}");
        expectedDependencies.add("org.codehaus.groovy:groovy-eclipse-batch:2.4.3-01");
        expectedDependencies.add("org.jetbrains.teamcity:server-api:${teamcity.version}");
        expectedDependencies.add("org.codehaus.groovy:groovy-eclipse-compiler:2.9.2-01");

        pluginDependencies.add("org.apache.maven.plugins:maven-assembly-plugin:2.6");
        pluginDependencies.add("org.apache.maven.plugins:maven-deploy-plugin:2.8.2");
        pluginDependencies.add("org.apache.maven.plugins:maven-surefire-report-plugin:2.19.1");
        pluginDependencies.add("org.apache.maven.plugins:maven-surefire-plugin:2.6");
        pluginDependencies.add("org.apache.maven.plugins:maven-enforcer-plugin:2.19.1");
    }

    @Test
    public void testParsingPomFile() throws Exception {
        final File pomInputStream = FunctionalTestFiles.asFile("/maven/hub-teamcity-pom.xml");
        final MavenParseExtractor pomXmlParser = new MavenParseExtractor(new ExternalIdFactory(), SAXParserFactory.newInstance().newSAXParser(), new MavenParseOptions(false));
        final Extraction extraction = pomXmlParser.extract(pomInputStream);
        final DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();

        final Set<String> externalIds = dependencyGraph.getRootDependencies().stream().map(dependency -> dependency.getExternalId().createExternalId()).collect(Collectors.toSet());
        assertEquals(expectedDependencies, externalIds);
    }

    @Test
    public void testIncludingPluginDependencies() throws Exception {
        final File pomInputStream = FunctionalTestFiles.asFile("/maven/hub-teamcity-pom.xml");
        final MavenParseExtractor pomXmlParser = new MavenParseExtractor(new ExternalIdFactory(), SAXParserFactory.newInstance().newSAXParser(), new MavenParseOptions(true));
        final Extraction extraction = pomXmlParser.extract(pomInputStream);
        final DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();

        final Set<String> externalIds = dependencyGraph.getRootDependencies().stream().map(dependency -> dependency.getExternalId().createExternalId()).collect(Collectors.toSet());
        assertTrue(externalIds.containsAll(expectedDependencies));
        assertTrue(externalIds.containsAll(pluginDependencies));
        assertEquals(expectedDependencies.size() + pluginDependencies.size(), externalIds.size());
    }

}
