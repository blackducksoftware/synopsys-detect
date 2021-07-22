package com.synopsys.integration.detectable.detectables.carthage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.extraction.Extraction;

public class CarthageExtractor {
    private static String GITHUB_SOURCE_ID = "github";

    private ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    public Extraction extract(File carthfileResolved) {
        MutableDependencyGraph dependencyGraph = new MutableMapDependencyGraph();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(carthfileResolved));
            String dependencyDeclaration = null;
            while ((dependencyDeclaration = reader.readLine()) != null) {
                // Each line in a Cartfile.resolved file is a dependency declaration: <origin> <name/resource> <version>
                // eg. github "realm/realm-cocoa" "v10.7.2"
                String[] pieces = dependencyDeclaration.split("\\s+");
                String origin = pieces[0];
                // Carthage supports declarations of dependencies via github org/repo, a URL, or a local path
                // As of now, Detect only supports dependencies with github origins
                // The KB does not have mappings for binaries, or resources that are not open source.  It has some mappings, though, for GitHub repos
                if (origin.equals(GITHUB_SOURCE_ID)) {
                    String name = pieces[1].replace("\"", "");
                    String version = pieces[2].replace("\"", "");

                    ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.GITHUB, name, version);
                    // As of Carthage 0.38.0 the dependencies in Cartfile.resolved are produced as a flat list
                    dependencyGraph.addChildToRoot(new Dependency(name, version, externalId));

                }
            }
        } catch (Exception e) {
            return new Extraction.Builder().failure(String.format("There was a problem extracting dependencies from %s", carthfileResolved.getAbsolutePath())).build();
        }
        CodeLocation codeLocation = new CodeLocation(dependencyGraph);
        // No project info - hoping git can help with that.
        return new Extraction.Builder().success(codeLocation).build();
    }
}
