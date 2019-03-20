package com.synopsys.integration.detectable.detectables.maven.functional;

import static org.junit.Assert.assertTrue;

import java.io.InputStream;

import org.junit.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.maven.parsing.parse.PomXmlParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class PomXmlParserTest {
    @Test
    public void testParsingPomFile() throws Exception {
        final InputStream pomInputStream = FunctionalTestFiles.asInputStream("/maven/hub-teamcity-pom.xml");
        final PomXmlParser pomXmlParser = new PomXmlParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = pomXmlParser.parse(pomInputStream).get();
        assertTrue(dependencyGraph.getRootDependencies().size() > 0);
    }
}
