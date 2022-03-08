package com.synopsys.integration.detectable.detectables.carthage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.carthage.model.CarthageDeclaration;
import com.synopsys.integration.detectable.detectables.carthage.parse.CartfileResolvedParser;
import com.synopsys.integration.detectable.detectables.carthage.transform.CarthageDeclarationTransformer;
import com.synopsys.integration.detectable.extraction.Extraction;

public class CarthageExtractor {
    private final CartfileResolvedParser cartfileResolvedParser;
    private final CarthageDeclarationTransformer declarationTransformer;

    public CarthageExtractor(CartfileResolvedParser cartfileResolvedParser, CarthageDeclarationTransformer declarationTransformer) {
        this.cartfileResolvedParser = cartfileResolvedParser;
        this.declarationTransformer = declarationTransformer;
    }

    public Extraction extract(File cartfileResolved) throws IOException {
        List<String> dependencyDeclarations = Files.readAllLines(cartfileResolved.toPath());
        List<CarthageDeclaration> carthageDeclarations = cartfileResolvedParser.parseDependencies(dependencyDeclarations);
        DependencyGraph dependencyGraph = declarationTransformer.transform(carthageDeclarations);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        // No project info - hoping git can help with that.
        return Extraction.success(codeLocation);
    }
}
