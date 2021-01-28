package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry.element;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.element.YarnLockDependencySpecParser;

public class YarnLockDependencySpecParserTest {

    @Test
    public void testNoColon() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser parser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);

        YarnLockDependency dep = parser.parse("\"@babel/helper-plugin-utils\" \"^7.8.0\"");

        assertEquals("@babel/helper-plugin-utils", dep.getName());
        assertEquals("^7.8.0", dep.getVersion());
    }

    @Test
    public void testWithColon() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser parser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);

        YarnLockDependency dep = parser.parse("\"@babel/helper-plugin-utils\": \"^7.8.0\"");

        assertEquals("@babel/helper-plugin-utils", dep.getName());
        assertEquals("^7.8.0", dep.getVersion());
    }

    @Test
    public void testUnquotedName() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser parser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);

        YarnLockDependency dep = parser.parse("property-expr \"^2.0.0\"");

        assertEquals("property-expr", dep.getName());
        assertEquals("^2.0.0", dep.getVersion());
    }
}
