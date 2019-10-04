package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalId;
import com.synopsys.integration.detectable.detectables.bazel.BazelCodeLocationBuilder;

public class BazelCodeLocationBuilderTest {

    @Test
    public void test() {
        BazelCodeLocationBuilder bdioBuilder = new BazelCodeLocationBuilder(new ExternalIdFactory());
        final List<CodeLocation> codeLocations = bdioBuilder
            .addDependency(BazelExternalId.fromBazelArtifactString("testGroup:testArtifact:testVersion", ":"))
            .build();

        assertEquals(1, codeLocations.size());
        assertEquals(1, codeLocations.get(0).getDependencyGraph().getRootDependencies().size());

        final Dependency dep = codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals("testArtifact", dep.name);
        assertEquals("testVersion", dep.version);
        assertEquals("testGroup", dep.externalId.group);
    }
}
