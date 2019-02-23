package com.synopsys.integration.detectable.detectables.bazel.functional.bazel;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.bazel.model.BazelExternalId;
import com.synopsys.integration.detectable.detectables.bazel.parse.BazelCodeLocationBuilder;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public class BazelCodeLocationBuilderTest {

    @Test
    public void test() {
        BazelCodeLocationBuilder bdioBuilder = new BazelCodeLocationBuilder(new ExternalIdFactory());
        final List<CodeLocation> codeLocations = bdioBuilder.setWorkspaceDir(FunctionalTestFiles.asFile("/bazel/multiLevel"))
            .addDependency(BazelExternalId.fromBazelArtifactString("testGroup:testArtifact:testVersion", ":"))
            .build();

        assertEquals(1, codeLocations.size());
        //assertEquals("src/test/resources/bazel/multiLevel", codeLocations.get(0).getExternalId().path); //TODO: Fails on windows because of slashes
        assertEquals(1, codeLocations.get(0).getDependencyGraph().getRootDependencies().size());

        Dependency dep = codeLocations.get(0).getDependencyGraph().getRootDependencies().iterator().next();
        assertEquals("testArtifact", dep.name);
        assertEquals("testVersion", dep.version);
        assertEquals("testGroup", dep.externalId.group);
    }
}
