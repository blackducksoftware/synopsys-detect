package com.synopsys.integration.detectable.detectables.yarn.unit.parse.entry.element;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.section.YarnLockDependencySpecParser;

class YarnLockDependencySpecParserTest {
    private static YarnLockDependencySpecParser parser;

    @BeforeAll
    static void setup() {
        YarnLockLineAnalyzer yarnLockLineAnalyzer = new YarnLockLineAnalyzer();
        parser = new YarnLockDependencySpecParser(yarnLockLineAnalyzer);
    }

    @Test
    void testNoColon() {
        doValidDependencyTest("\"@babel/helper-plugin-utils\" \"^7.8.0\"", "@babel/helper-plugin-utils", "^7.8.0");
    }

    @Test
    void testWithColon() {
        doValidDependencyTest("\"@babel/helper-plugin-utils\": \"^7.8.0\"", "@babel/helper-plugin-utils", "^7.8.0");
    }

    @Test
    void testUnquotedName() {
        doValidDependencyTest("property-expr \"^2.0.0\"", "property-expr", "^2.0.0");
    }

    @Test
    void testQuotedMultipleVersion() {
        doValidDependencyTest("xtend \">=4.0.0 <4.1.0-0\"", "xtend", ">=4.0.0 <4.1.0-0");
    }

    @Test
    void testWorkspaceDependency() {
        doValidDependencyTest("\"@yarnpkg/plugin-npm\": \"workspace:^2.4.0\"", "@yarnpkg/plugin-npm", "workspace:^2.4.0");
    }

    @Test
    void testSkippableProtocols() {
        Optional<YarnLockDependency> dep = parser.parse("gatsby-plugin-algolia-docsearch: \"portal:./gatsby-plugin-algolia-docsearch\"", true);
        Assertions.assertFalse(dep.isPresent());

        dep = parser.parse("gatsby-plugin-algolia-docsearch: \"link:./gatsby-plugin-algolia-docsearch\"", true);
        Assertions.assertFalse(dep.isPresent());

        dep = parser.parse("gatsby-plugin-algolia-docsearch: \"patch:./gatsby-plugin-algolia-docsearch\"", true);
        Assertions.assertFalse(dep.isPresent());
    }

    private void doValidDependencyTest(String dependencySpec, String expectedName, String expectedVersion) {
        Optional<YarnLockDependency> dep = parser.parse(dependencySpec, true);
        Assertions.assertTrue(dep.isPresent());
        YarnLockDependency gottenDep = dep.get();
        Assertions.assertEquals(expectedName, gottenDep.getName());
        Assertions.assertEquals(expectedVersion, gottenDep.getVersion());
    }
}
