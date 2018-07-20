package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;

public class PipEnvTreeParserTest {
    private PipenvGraphParser parser;

    private final String name = "urllib3";
    private final String version = "1.22";
    private final String dependencyName = "- urllib3 [required: <1.23,==1.21.1, installed: 1.22]";
    private final String line1 = PipenvGraphParser.DEPENDENCY_INDENTATION + dependencyName;
    private final String line2 = PipenvGraphParser.DEPENDENCY_INDENTATION + PipenvGraphParser.DEPENDENCY_INDENTATION + line1;
    private final String line3 = "invalid line";

    @Before
    public void init() {
        parser = new PipenvGraphParser(new ExternalIdFactory());
    }

    @Test
    public void getCurrentIndentationTest() {
        final int indentation1 = parser.getLevel(line1);
        assertEquals(1, indentation1);

        final int indentation2 = parser.getLevel(line2);
        assertEquals(3, indentation2);
    }

    @Test
    public void lineToNodeTest() {
        final Map<String, String[]> pipFreezeMap = new HashMap<>();

        final Optional<Dependency> validNode1 = parser.getDependencyFromLine(pipFreezeMap, line1);
        assertTrue(validNode1.isPresent());
        assertEquals(name, validNode1.get().name);
        assertEquals(version, validNode1.get().version);

        final Optional<Dependency> validNode2 = parser.getDependencyFromLine(pipFreezeMap, line2);
        assertTrue(validNode2.isPresent());
        assertEquals(validNode1.get().name, validNode2.get().name);
        assertEquals(validNode1.get().version, validNode2.get().version);

        final Optional<Dependency> invalidNode = parser.getDependencyFromLine(pipFreezeMap, line3);
        assertFalse(invalidNode.isPresent());
    }

    @Test
    public void invalidParseTest() {
        final List<String> pipFreezeOutput = new ArrayList<>();

        String invalidText = "i am not a valid file" + "\n";
        invalidText += "the result should be null";

        final PipParseResult root = parser.parse(BomToolType.PIP_ENV, "name", "version", pipFreezeOutput, Arrays.asList(invalidText.split("\r?\n")), "");
        assertNull(root);
    }
}
