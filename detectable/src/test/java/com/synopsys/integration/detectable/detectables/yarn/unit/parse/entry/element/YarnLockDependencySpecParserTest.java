package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry.element;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockDependencySpecParser;

public class YarnLockDependencySpecParserTest {
    private static YarnLockDependencySpecParser parser;

    @BeforeAll
    static void setup() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        parser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);
    }

    @Test
    public void testNoColon() {
        YarnLockDependency dep = parser.parse("\"@babel/helper-plugin-utils\" \"^7.8.0\"", true);

        assertEquals("@babel/helper-plugin-utils", dep.getName());
        assertEquals("^7.8.0", dep.getVersion());
    }

    @Test
    public void testWithColon() {
        YarnLockDependency dep = parser.parse("\"@babel/helper-plugin-utils\": \"^7.8.0\"", true);

        assertEquals("@babel/helper-plugin-utils", dep.getName());
        assertEquals("^7.8.0", dep.getVersion());
    }

    @Test
    public void testUnquotedName() {
        YarnLockDependency dep = parser.parse("property-expr \"^2.0.0\"", true);

        assertEquals("property-expr", dep.getName());
        assertEquals("^2.0.0", dep.getVersion());
    }

    @Test
    void testQuotedMultipleVersion() {
        YarnLockDependency dep = parser.parse("xtend \">=4.0.0 <4.1.0-0\"", true);

        assertEquals("xtend", dep.getName());
        assertEquals(">=4.0.0 <4.1.0-0", dep.getVersion());
    }
}
