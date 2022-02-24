package com.synopsys.integration.detectable.detectables.carthage;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.detectable.detectables.carthage.model.CarthageDeclaration;
import com.synopsys.integration.detectable.detectables.carthage.transform.CarthageDeclarationTransformer;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

class CarthageDeclarationTransformerTest {

    @Test
    void happyCase() {
        CarthageDeclarationTransformer transformer = new CarthageDeclarationTransformer();
        List<CarthageDeclaration> carthageDeclarations = Arrays.asList(
            new CarthageDeclaration("github", "some-name/resource", "some-version"),
            new CarthageDeclaration("github", "different-name/resource", "other-version")
        );

        DependencyGraph graph = transformer.transform(carthageDeclarations);
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, graph);

        graphAssert.hasRootDependency("some-name/resource", "some-version");
        graphAssert.hasRootDependency("different-name/resource", "other-version");
        graphAssert.hasRootSize(2);
    }

    @Test
    void excludeNonGitHubOrigin() {
        CarthageDeclarationTransformer transformer = new CarthageDeclarationTransformer();
        List<CarthageDeclaration> carthageDeclarations = Arrays.asList(
            new CarthageDeclaration("binary", "https://some.binary.url", "binary-version"),
            new CarthageDeclaration("github", "some-name/resource", "some-version"),
            new CarthageDeclaration("wonky-origin", "wonky-name", "wonky-version")
        );

        DependencyGraph graph = transformer.transform(carthageDeclarations);
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.GITHUB, graph);

        graphAssert.hasRootDependency("some-name/resource", "some-version");
        graphAssert.hasNoDependency("https://some.binary.url", "binary-version");
        graphAssert.hasNoDependency("wonky-name", "wonky-version");
        graphAssert.hasRootSize(1);
    }
}