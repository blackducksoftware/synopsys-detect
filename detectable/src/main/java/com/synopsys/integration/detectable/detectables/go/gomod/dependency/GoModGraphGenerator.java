package com.synopsys.integration.detectable.detectables.go.gomod.dependency;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;

public class GoModGraphGenerator {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ExternalIdFactory externalIdFactory;
    private final Set<String> fullyGraphedModules = new HashSet<>();

    public GoModGraphGenerator(ExternalIdFactory externalIdFactory) {
        this.externalIdFactory = externalIdFactory;
    }

    public CodeLocation generateGraph(GoListModule projectModule, GoRelationshipWalker goRelationshipWalker, GoVersionManager goVersionManager) {
        MutableDependencyGraph graph = new MutableMapDependencyGraph();
        String moduleName = projectModule.getPath();
        if (goRelationshipWalker.hasRelationshipsFor(moduleName)) {
            goRelationshipWalker.getRelationshipsFor(moduleName).stream()
                .map(relationship -> relationship.getChild().getName())
                .forEach(childName -> addModuleToGraph(childName, null, graph, goRelationshipWalker, goVersionManager));
        }

        return new CodeLocation(graph, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, projectModule.getPath(), projectModule.getVersion()));
    }

    private void addModuleToGraph(String moduleName, @Nullable Dependency parent, MutableDependencyGraph graph, GoRelationshipWalker goRelationshipWalker, GoVersionManager goVersionManager) {
        if (!goRelationshipWalker.shouldIncludeModule(moduleName)) {
            logger.debug("Excluding module '{}' because it is not used in source.", moduleName);
            return;
        }

        String moduleVersion = goVersionManager.getVersionForModule(moduleName).orElse(null);
        Dependency dependency = convertToDependency(moduleName, moduleVersion);
        if (parent != null) {
            graph.addChildWithParent(dependency, parent);
        } else {
            graph.addChildToRoot(dependency);
        }

        if (!fullyGraphedModules.contains(moduleName) && goRelationshipWalker.hasRelationshipsFor(moduleName)) {
            fullyGraphedModules.add(moduleName);
            List<GoGraphRelationship> projectRelationships = goRelationshipWalker.getRelationshipsFor(moduleName);
            for (GoGraphRelationship projectRelationship : projectRelationships) {
                addModuleToGraph(projectRelationship.getChild().getName(), dependency, graph, goRelationshipWalker, goVersionManager);
            }
        }
    }

    private void addChildFromRelationship(GoGraphRelationship relationship, MutableDependencyGraph graph, GoVersionManager goVersionManager) {
        String childName = relationship.getChild().getName();
        String parentName = relationship.getParent().getName();
        if (parentName.equals(childName)) {
            // Resolved versions can appear as a relationship.
            // Example: github.com/dgrijalva/jwt-go github.com/dgrijalva/jwt-go@v3.2.0+incompatible
            return;
        }

        String parentVersion = goVersionManager.getVersionForModule(parentName).orElse(null);
        String childVersion = goVersionManager.getVersionForModule(childName).orElse(null);
        Dependency childDependency = convertToDependency(childName, childVersion);
        Dependency parentDependency = convertToDependency(parentName, parentVersion);
        graph.addParentWithChild(parentDependency, childDependency);
    }

    private Dependency convertToDependency(String moduleName, @Nullable String moduleVersion) {
        return new Dependency(moduleName, moduleVersion, externalIdFactory.createNameVersionExternalId(Forge.GOLANG, moduleName, moduleVersion));
    }
}
