package com.synopsys.integration.detectable.detectables.maven.functional;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.junit.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.maven.parsing.MavenParseExtractor;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class PomXmlParserTest {
    @Test
    public void testParsingPomFile() throws Exception {
        final File pomInputStream = FunctionalTestFiles.asFile("/maven/hub-teamcity-pom.xml");
        final MavenParseExtractor pomXmlParser = new MavenParseExtractor(new ExternalIdFactory(), SAXParserFactory.newInstance().newSAXParser());
        final Extraction extraction = pomXmlParser.extract(pomInputStream);
        DependencyGraph dependencyGraph = extraction.getCodeLocations().get(0).getDependencyGraph();
        assertTrue(dependencyGraph.getRootDependencies().size() > 0);
    }
}
