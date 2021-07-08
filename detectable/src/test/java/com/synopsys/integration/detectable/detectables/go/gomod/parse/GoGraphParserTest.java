package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;

class GoGraphParserTest {
    @Test
    void happyPath() {
        List<GoGraphRelationship> relationships = generateRelationships(
            "example.io/moduleA example.io/moduleB@1.0.0",
            "example.io/moduleB example.io/moduleC@2.0.0"
        );
        assertEquals(2, relationships.size());

        GoGraphRelationship relationship1 = relationships.get(0);
        assertEquals("example.io/moduleA", relationship1.getParent().getName());
        assertNull(relationship1.getParent().getVersion());
        assertEquals("example.io/moduleB", relationship1.getChild().getName());
        assertEquals("1.0.0", relationship1.getChild().getVersion());

        GoGraphRelationship relationship2 = relationships.get(1);
        assertEquals("example.io/moduleB", relationship2.getParent().getName());
        assertNull(relationship2.getParent().getVersion());
        assertEquals("example.io/moduleC", relationship2.getChild().getName());
        assertEquals("2.0.0", relationship2.getChild().getVersion());
    }

    @Test
    void versionWithIncompatible() {
        List<GoGraphRelationship> relationships = generateRelationships(
            "example.io/moduleA example.io/incompatible@2.0.0+incompatible"
        );
        assertEquals(1, relationships.size());

        GoGraphRelationship relationship = relationships.get(0);
        assertEquals("example.io/moduleA", relationship.getParent().getName());
        assertNull(relationship.getParent().getVersion());
        assertEquals("example.io/incompatible", relationship.getChild().getName());
        assertEquals("2.0.0", relationship.getChild().getVersion());
    }

    @Test
    void versionWithGitHash() {
        List<GoGraphRelationship> relationships = generateRelationships(
            "example.io/moduleA example.io/hash@version_with_hash-123abc456"
        );
        assertEquals(1, relationships.size());

        GoGraphRelationship relationship = relationships.get(0);
        assertEquals("example.io/moduleA", relationship.getParent().getName());
        assertNull(relationship.getParent().getVersion());
        assertEquals("example.io/hash", relationship.getChild().getName());
        assertEquals("123abc456", relationship.getChild().getVersion());
    }

    @Test
    void unParseableLine() {
        List<GoGraphRelationship> relationships = generateRelationships(
            "example.io/moduleB example.io/invalid@2.0.0 un-parseable line"
        );
        assertEquals(0, relationships.size());
    }

    @Test
    void invalidVersion() {
        List<GoGraphRelationship> relationships = generateRelationships(
            "example.io/moduleA example.io/invalid@2.0.0@invalid_name_version"
        );
        assertEquals(1, relationships.size());

        GoGraphRelationship relationship = relationships.get(0);
        assertEquals("example.io/moduleA", relationship.getParent().getName());
        assertNull(relationship.getParent().getVersion());
        assertEquals("example.io/invalid@2.0.0@invalid_name_version", relationship.getChild().getName());
        assertNull(relationship.getChild().getVersion());
    }

    private List<GoGraphRelationship> generateRelationships(String... goModGraphOutput) {
        GoGraphParser goGraphParser = new GoGraphParser();
        return goGraphParser.parseRelationshipsFromGoModGraph(Arrays.asList(goModGraphOutput));
    }
}
