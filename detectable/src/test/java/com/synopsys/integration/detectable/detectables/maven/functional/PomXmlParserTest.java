package com.synopsys.integration.detectable.detectables.maven.functional;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.maven.parsing.parse.PomDocumentParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class PomXmlParserTest {
    @Test
    public void testParsingPomFile() throws Exception {
        final File pomInputStream = FunctionalTestFiles.asFile("/maven/hub-teamcity-pom.xml");

        PomDocumentParser pomDocumentParser = new PomDocumentParser(new ExternalIdFactory(), DocumentBuilderFactory.newInstance().newDocumentBuilder());
        List<Dependency> dependencies = pomDocumentParser.parse(pomInputStream);

        assertEquals(9, dependencies.size());
    }
}
