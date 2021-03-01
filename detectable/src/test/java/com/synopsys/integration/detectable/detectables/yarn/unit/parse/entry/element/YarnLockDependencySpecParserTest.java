package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry.element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

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
        Optional<YarnLockDependency> dep = parser.parse("\"@babel/helper-plugin-utils\" \"^7.8.0\"", true);
        assertTrue(dep.isPresent());
        YarnLockDependency gottenDep = dep.get();
        assertEquals("@babel/helper-plugin-utils", gottenDep.getName());
        assertEquals("^7.8.0", gottenDep.getVersion());
    }

    @Test
    public void testWithColon() {
        Optional<YarnLockDependency> dep = parser.parse("\"@babel/helper-plugin-utils\": \"^7.8.0\"", true);
        assertTrue(dep.isPresent());
        YarnLockDependency gottenDep = dep.get();
        assertEquals("@babel/helper-plugin-utils", gottenDep.getName());
        assertEquals("^7.8.0", gottenDep.getVersion());
    }

    @Test
    public void testUnquotedName() {
        Optional<YarnLockDependency> dep = parser.parse("property-expr \"^2.0.0\"", true);
        assertTrue(dep.isPresent());
        YarnLockDependency gottenDep = dep.get();
        assertEquals("property-expr", gottenDep.getName());
        assertEquals("^2.0.0", gottenDep.getVersion());
    }

    @Test
    void testQuotedMultipleVersion() {
        Optional<YarnLockDependency> dep = parser.parse("xtend \">=4.0.0 <4.1.0-0\"", true);
        assertTrue(dep.isPresent());
        YarnLockDependency gottenDep = dep.get();
        assertEquals("xtend", gottenDep.getName());
        assertEquals(">=4.0.0 <4.1.0-0", gottenDep.getVersion());
    }

    @Test
    void testWorkspaceDependency() {
        Optional<YarnLockDependency> dep = parser.parse("\"@yarnpkg/plugin-npm\": \"workspace:^2.4.0\"", true);
        assertTrue(dep.isPresent());
        YarnLockDependency gottenDep = dep.get();
        assertEquals("@yarnpkg/plugin-npm", gottenDep.getName());
        assertEquals("workspace:^2.4.0", gottenDep.getVersion());
    }

    @Test
    void testPatchPortalLink() {
        Optional<YarnLockDependency> dep = parser.parse("gatsby-plugin-algolia-docsearch: \"portal:./gatsby-plugin-algolia-docsearch\"", true);
        assertFalse(dep.isPresent());

        dep = parser.parse("gatsby-plugin-algolia-docsearch: \"link:./gatsby-plugin-algolia-docsearch\"", true);
        assertFalse(dep.isPresent());

        dep = parser.parse("gatsby-plugin-algolia-docsearch: \"patch:./gatsby-plugin-algolia-docsearch\"", true);
        assertFalse(dep.isPresent());
    }
}
