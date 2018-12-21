package com.blackducksoftware.integration.hub.detect.detector.bazel;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.blackducksoftware.integration.hub.detect.workflow.codelocation.DetectCodeLocation;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class BazelBdioBuilderTest {

    @Test
    public void test() {
        BazelBdioBuilder bdioBuilder = new BazelBdioBuilder(new ExternalIdFactory());
        final List<DetectCodeLocation> codeLocations = bdioBuilder.setWorkspaceDir(new File("src/test/resources/bazel/multiLevel"))
            .addDependency(BazelExternalId.fromBazelArtifactString("testGroup:testArtifact:testVersion", ":"))
            .build();

        assertEquals(1, codeLocations.size());
        assertEquals("multiLevel", codeLocations.get(0).getExternalId().name);
        assertEquals("unknown", codeLocations.get(0).getExternalId().version);
        assertEquals(1, codeLocations.get(0).getDependencyGraph().getRootDependencies().size());

        Dependency dep = codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals("testArtifact", dep.name);
        assertEquals("testVersion", dep.version);
        assertEquals("testGroup", dep.externalId.group);
    }
}
