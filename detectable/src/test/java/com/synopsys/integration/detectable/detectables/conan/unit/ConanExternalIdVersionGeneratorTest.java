package com.synopsys.integration.detectable.detectables.conan.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.conan.ConanExternalIdVersionGenerator;
import com.synopsys.integration.detectable.detectables.conan.graph.GenericNode;
import com.synopsys.integration.detectable.detectables.conan.graph.GenericNodeBuilder;

public class ConanExternalIdVersionGeneratorTest {

    @Test
    public void testMinimal() {
        ConanExternalIdVersionGenerator generator = new ConanExternalIdVersionGenerator();
        GenericNodeBuilder<String> nodeBuilder = new GenericNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3");
        nodeBuilder.setVersion("1.2.3");
        Optional<GenericNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = generator.generateExternalIdVersionString(node.get(), false);

        assertEquals("1.2.3@_/_#0", version);
    }

    @Test
    public void testShortForm() {
        ConanExternalIdVersionGenerator generator = new ConanExternalIdVersionGenerator();
        GenericNodeBuilder<String> nodeBuilder = new GenericNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        Optional<GenericNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = generator.generateExternalIdVersionString(node.get(), false);

        assertEquals("1.2.3@testuser/testchannel#testrrev", version);
    }

    @Test
    public void testShortFormWhenLongFormPossible() {
        ConanExternalIdVersionGenerator generator = new ConanExternalIdVersionGenerator();
        GenericNodeBuilder<String> nodeBuilder = new GenericNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        nodeBuilder.setPackageId("testpkgid");
        nodeBuilder.setPackageRevision("testprev");
        Optional<GenericNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = generator.generateExternalIdVersionString(node.get(), false);

        assertEquals("1.2.3@testuser/testchannel#testrrev", version);
    }

    @Test
    public void testLongForm() {
        ConanExternalIdVersionGenerator generator = new ConanExternalIdVersionGenerator();
        GenericNodeBuilder<String> nodeBuilder = new GenericNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        nodeBuilder.setPackageId("testpkgid");
        nodeBuilder.setPackageRevision("testprev");
        Optional<GenericNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = generator.generateExternalIdVersionString(node.get(), true);

        assertEquals("1.2.3@testuser/testchannel#testrrev:testpkgid#testprev", version);
    }

    @Test
    public void testShortFormWhenLongFormRequested() {
        ConanExternalIdVersionGenerator generator = new ConanExternalIdVersionGenerator();
        GenericNodeBuilder<String> nodeBuilder = new GenericNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        nodeBuilder.setPackageId("testpkgid");
        // Prev is not available: should fall back to short form
        Optional<GenericNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = generator.generateExternalIdVersionString(node.get(), true);

        assertEquals("1.2.3@testuser/testchannel#testrrev", version);
    }
}
