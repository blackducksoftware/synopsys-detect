package com.synopsys.integration.detectable.detectables.maven.functional;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseExtractor;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MavenParseExtractorTest {
    private Set<String> expectedDependencies = new HashSet<>();
    private Set<String> pluginDependencies = new HashSet<>();

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
        final MavenParseExtractor pomXmlParser = new MavenParseExtractor(new ExternalIdFactory(), false, SAXParserFactory.newInstance().newSAXParser());
        final Extraction extraction = pomXmlParser.extract(pomInputStream);
        DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();

        Set<String> externalIds = dependencyGraph.getRootDependencies().stream().map(dependency -> dependency.externalId.createExternalId()).collect(Collectors.toSet());
        assertEquals(expectedDependencies, externalIds);
    }

    @Test
    public void testIncludingPluginDependencies() throws Exception {
        final File pomInputStream = FunctionalTestFiles.asFile("/maven/hub-teamcity-pom.xml");
        final MavenParseExtractor pomXmlParser = new MavenParseExtractor(new ExternalIdFactory(), true, SAXParserFactory.newInstance().newSAXParser());
        final Extraction extraction = pomXmlParser.extract(pomInputStream);
        DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();

        Set<String> externalIds = dependencyGraph.getRootDependencies().stream().map(dependency -> dependency.externalId.createExternalId()).collect(Collectors.toSet());
        assertTrue(externalIds.containsAll(expectedDependencies));
        assertTrue(externalIds.containsAll(pluginDependencies));
        assertEquals(expectedDependencies.size() + pluginDependencies.size(), externalIds.size());
    }

}
