package com.blackducksoftware.integration.hub.detect.bomtool.cran;

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph;
import com.blackducksoftware.integration.hub.bdio.graph.builder.LazyExternalIdDependencyGraphBuilder;
import com.blackducksoftware.integration.hub.bdio.model.Forge;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.DependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.dependencyid.NameVersionDependencyId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;

import java.util.List;

public class PackRatNodeParser {
    private final  ExternalIdFactory externalIdFactory;

    public PackRatNodeParser(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    DependencyGraph parseProjectDependencies(final List<String> packratLockContents) {
        LazyExternalIdDependencyGraphBuilder graphBuilder = new LazyExternalIdDependencyGraphBuilder();

        DependencyId currentParent = null;
        String name = null;
        String version = null;

        for (String line : packratLockContents) {
            if (line.startsWith("PackratFormat:")) {
                continue;
            } else if (line.startsWith("PackratVersion:")) {
                continue;
            } else if (line.startsWith("RVersion:")) {
                continue;
            }

            if (line.contains("Package: ")) {
                name = line.replace("Package: ", "").trim();
                currentParent = new NameDependencyId(name);
                graphBuilder.setDependencyName(currentParent, name);
                graphBuilder.addChildToRoot(currentParent);
                version = null;
                continue;
            }

            if (line.contains("Version: ")) {
                version = line.replace("Version: ", "").trim();
                graphBuilder.setDependencyVersion(currentParent, version);
                DependencyId realId = new NameVersionDependencyId(name, version);
                ExternalId externalId = externalIdFactory.createNameVersionExternalId(Forge.CRAN, name, version);
                graphBuilder.setDependencyAsAlias(realId, currentParent);
                graphBuilder.setDependencyInfo(realId, name, version, externalId);
                currentParent = realId;
            }

            if (line.contains("Requires: ")) {
                String[] parts = line.replace("Requires: ","").split(",");
                for (int i = 0; i < parts.length; i++) {
                    String childName = parts[i].trim();
                    graphBuilder.addParentWithChild(currentParent, new NameDependencyId(childName));
                }
            }
        }

        return graphBuilder.build();
    }

    public ExternalIdFactory getExternalIdFactory() {
        return externalIdFactory;
    }

}
