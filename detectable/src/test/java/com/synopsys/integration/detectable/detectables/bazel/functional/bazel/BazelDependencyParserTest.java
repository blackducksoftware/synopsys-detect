package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.bazel.BazelDependencyParser;

public class BazelDependencyParserTest {

    @Test
    public void test() {
        final ExternalIdFactory externalIdFactory = Mockito.mock(ExternalIdFactory.class);
        final BazelDependencyParser bazelDependencyParser = new BazelDependencyParser(externalIdFactory);
        // externalIdFactory.createMavenExternalId(group, artifact, version);
        final ExternalId testExternalId = new ExternalId(Forge.MAVEN);
        testExternalId.setGroup("testgroup");
        testExternalId.setName("testartifact");
        testExternalId.setVersion("testversion");
        Mockito.when(externalIdFactory.createMavenExternalId("testgroup", "testartifact", "testversion")).thenReturn(testExternalId);

        final Dependency dependency = bazelDependencyParser.gavStringToDependency("testgroup:testartifact:testversion", ":");

        assertEquals("testartifact", dependency.getExternalId().getName());
    }
}
