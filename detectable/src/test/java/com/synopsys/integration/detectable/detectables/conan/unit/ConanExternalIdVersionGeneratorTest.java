package com.synopsys.integration.detectable.detectables.conan.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectables.conan.ConanExternalIdVersionGenerator;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNode;
import com.synopsys.integration.detectable.detectables.conan.graph.ConanNodeBuilder;
import com.synopsys.integration.exception.IntegrationException;

public class ConanExternalIdVersionGeneratorTest {

    @Test
    public void testMinimal() throws IntegrationException {
        ConanNodeBuilder<String> nodeBuilder = new ConanNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3");
        nodeBuilder.setVersion("1.2.3");
        Optional<ConanNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = ConanExternalIdVersionGenerator.generateExternalIdVersionString(node.get(), false);

        assertEquals("1.2.3@_/_#0", version);
    }

    @Test
    public void testShortForm() throws IntegrationException {
        ConanNodeBuilder<String> nodeBuilder = new ConanNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        Optional<ConanNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = ConanExternalIdVersionGenerator.generateExternalIdVersionString(node.get(), false);

        assertEquals("1.2.3@testuser/testchannel#testrrev", version);
    }

    @Test
    public void testShortFormWhenLongFormPossible() throws IntegrationException {
        ConanNodeBuilder<String> nodeBuilder = new ConanNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        nodeBuilder.setPackageId("testpkgid");
        nodeBuilder.setPackageRevision("testprev");
        Optional<ConanNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = ConanExternalIdVersionGenerator.generateExternalIdVersionString(node.get(), false);

        assertEquals("1.2.3@testuser/testchannel#testrrev", version);
    }

    @Test
    public void testLongForm() throws IntegrationException {
        ConanNodeBuilder<String> nodeBuilder = new ConanNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        nodeBuilder.setPackageId("testpkgid");
        nodeBuilder.setPackageRevision("testprev");
        Optional<ConanNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = ConanExternalIdVersionGenerator.generateExternalIdVersionString(node.get(), true);

        assertEquals("1.2.3@testuser/testchannel#testrrev:testpkgid#testprev", version);
    }

    @Test
    public void testShortFormWhenLongFormRequested() throws IntegrationException {
        ConanNodeBuilder<String> nodeBuilder = new ConanNodeBuilder<>();
        nodeBuilder.setRef("bzip/1.2.3@testuser/testchannel");
        nodeBuilder.setVersion("1.2.3");
        nodeBuilder.setUser("testuser");
        nodeBuilder.setChannel("testchannel");
        nodeBuilder.setRecipeRevision("testrrev");
        nodeBuilder.setPackageId("testpkgid");
        // Prev is not available: should fall back to short form
        Optional<ConanNode<String>> node = nodeBuilder.build();
        assertTrue(node.isPresent());

        String version = ConanExternalIdVersionGenerator.generateExternalIdVersionString(node.get(), true);

        assertEquals("1.2.3@testuser/testchannel#testrrev", version);
    }
}
