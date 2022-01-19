package com.synopsys.integration.detectable.detectables.carthage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class CarthageExtractor {
    private final CartfileResolvedDependencyDeclarationParser dependencyDeclarationParser;

    public CarthageExtractor(CartfileResolvedDependencyDeclarationParser dependencyDeclarationParser) {
        this.dependencyDeclarationParser = dependencyDeclarationParser;
    }

    public Extraction extract(File cartfileResolved) throws IOException {
        List<String> dependencyDeclarations = Files.readAllLines(cartfileResolved.toPath());
        DependencyGraph dependencyGraph = dependencyDeclarationParser.parseDependencies(dependencyDeclarations);
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        // No project info - hoping git can help with that.
        return new Extraction.Builder().success(codeLocation).build();
    }
}
