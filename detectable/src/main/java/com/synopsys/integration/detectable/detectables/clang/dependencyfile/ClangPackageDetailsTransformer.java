package com.synopsys.integration.detectable.detectables.clang.dependencyfile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.clang.packagemanager.PackageDetails;

public class ClangPackageDetailsTransformer {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ExternalIdFactory externalIdFactory;

    public ClangPackageDetailsTransformer(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation toCodeLocation(List<Forge> dependencyForges, Set<PackageDetails> packages) {
        List<Dependency> dependencies = packages.parallelStream()
            .flatMap(pkg -> toDependency(dependencyForges, pkg).stream())
            .collect(Collectors.toList());
        logger.trace("Generated : {} dependencies.", dependencies.size());

        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        dependencyGraph.addChildrenToRoot(dependencies);

        return new CodeLocation(dependencyGraph);
    }

    private List<Dependency> toDependency(List<Forge> forges, PackageDetails details) {
        String name = details.getPackageName();
        String version = details.getPackageVersion();
        String arch = details.getPackageArch();

        List<Dependency> dependencies = new ArrayList<>();
        String externalId = String.format("%s/%s/%s", name, version, arch);
        logger.trace(String.format("Constructed externalId: %s", externalId));
        for (Forge forge : forges) {
            ExternalId extId = externalIdFactory.createArchitectureExternalId(forge, name, version, arch);
            Dependency dep = new Dependency(name, version, extId);
            logger.debug(String.format(
                "forge: %s: adding %s version %s as child to dependency node tree; externalId: %s",
                forge.getName(),
                dep.getName(),
                dep.getVersion(),
                dep.getExternalId().createBdioId()
            ));
            dependencies.add(dep);
        }
        return dependencies;
    }
}
