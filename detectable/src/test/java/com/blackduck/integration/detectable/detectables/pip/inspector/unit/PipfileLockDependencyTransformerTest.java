package com.blackduck.integration.detectable.detectables.pip.inspector.unit;

import java.util.Arrays;
import java.util.List;

import com.blackduck.integration.detectable.detectables.pipenv.parse.PipfileLockDependencyTransformer;
import com.blackduck.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;
import com.blackduck.integration.detectable.util.graph.NameVersionGraphAssert;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;

public class PipfileLockDependencyTransformerTest {
    @Test
    public void testTransform() {
        List<PipfileLockDependency> dependencies = Arrays.asList(
            new PipfileLockDependency("comp1", "1.0"),
            new PipfileLockDependency("comp2", "2.0")
        );

        PipfileLockDependencyTransformer transformer = new PipfileLockDependencyTransformer();
        DependencyGraph dependencyGraph = transformer.transform(dependencies);
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, dependencyGraph);

        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("comp1", "1.0");
        graphAssert.hasRootDependency("comp2", "2.0");
    }
}
