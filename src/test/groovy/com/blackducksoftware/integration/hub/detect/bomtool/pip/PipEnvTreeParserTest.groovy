package com.blackducksoftware.integration.hub.detect.bomtool.pip

import static org.junit.Assert.*;

import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.pip.parse.PipenvTreeParser
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

class PipEnvTreeParserTest {
    private PipenvTreeParser parser
    private TestUtil testUtil = new TestUtil()

    private String name = 'urllib3'
    private String version = '1.22'
    private String fullName = name + PipenvTreeParser.TOP_LEVEL_SEPARATOR + version
    private String dependencyName = "- urllib3 [required: <1.23,==1.21.1, installed: 1.22]"
    private String line1 = PipenvTreeParser.DEPENDENCY_INDENTATION + dependencyName
    private String line2 = PipenvTreeParser.DEPENDENCY_INDENTATION.multiply(2) + line1
    private String line3 = 'invalid line'

    @Before
    void init() {
        parser = new PipenvTreeParser()
        parser.externalIdFactory = new ExternalIdFactory()
    }

    @Test
    void getCurrentIndentationTest() {
        int indentation1 = parser.getLevel(line1)
        assertEquals(1, indentation1)

        int indentation2 = parser.getLevel(line2)
        assertEquals(3, indentation2)
    }

    @Test
    void lineToNodeTest() {
        Optional<Dependency> validNode1 = parser.getDependencyFromLine(line1)
        assertTrue(validNode1.isPresent())
        assertEquals(name, validNode1.get().name)
        assertEquals(version, validNode1.get().version)

        Optional<Dependency> validNode2 = parser.getDependencyFromLine(line2)
        assertTrue(validNode2.isPresent())
        assertEquals(validNode1.get().name, validNode2.get().name)
        assertEquals(validNode1.get().version, validNode2.get().version)

        Optional<Dependency> invalidNode = parser.getDependencyFromLine(line3)
        assertFalse(invalidNode.isPresent())
    }


    @Test
    void invalidParseTest() {
        def invalidText = """
        i am not a valid file
        the result should be null
        """
        invalidText = invalidText.split("\r?\n").join(System.lineSeparator)
        DetectCodeLocation root = parser.parse("name", "version", invalidText, '')
        assertNull(root)
    }
}
