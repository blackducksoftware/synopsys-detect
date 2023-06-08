package com.synopsys.integration.detectable.detectables.dart.pubdep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.google.gson.*;
import com.synopsys.integration.detectable.detectables.dart.pubdep.model.DartPubDep;

import com.synopsys.integration.bdio.graph.BasicDependencyGraph;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class PubDepsParser {
    public DependencyGraph parse(String pubDepsOutput) {
        JsonArray dependencies = getDependencies(pubDepsOutput);

        Map<String, DartPubDep> dependencyMap = new HashMap<>();
        
        DartPubDep rootDep = createDependencyMapAndGetRootDep(dependencies, dependencyMap);

        createDependencyHierarchy(dependencies, dependencyMap);


        DependencyGraph dependencyGraph = new BasicDependencyGraph();
        generateDependencyGraph(dependencyGraph, null, rootDep);

        return dependencyGraph;
    }

    private void createDependencyHierarchy(JsonArray dependencies, Map<String, DartPubDep> dependencyMap) {
        dependencies.forEach(dep -> {
            JsonObject obj = dep.getAsJsonObject();
            JsonArray deps = obj.get("dependencies").getAsJsonArray();
            DartPubDep parent = dependencyMap.get(obj.get("name").getAsString());
            deps.forEach(d -> {
                String childDep = d.getAsString();
                DartPubDep child = dependencyMap.get(childDep);
                parent.dependencies.add(child);
            });
        });
    }

    private DartPubDep createDependencyMapAndGetRootDep(JsonArray dependencies, Map<String, DartPubDep> dependencyModel) {
        AtomicReference<DartPubDep> rootDep = new AtomicReference<>();

        dependencies.forEach(dep -> {
            JsonObject obj = dep.getAsJsonObject();
            DartPubDep model = new DartPubDep();
            model.name = obj.get("name").getAsString();
            model.version = obj.get("version").getAsString();
            String kind = obj.get("kind").getAsString();

            if ("root".equalsIgnoreCase(kind)) {
                rootDep.set(model);
            }

            dependencyModel.put(obj.get("name").getAsString(), model);
        });
        return rootDep.get();
    }

    private JsonArray getDependencies(String pubDepsOutput) {
        JsonObject jsonObject = JsonParser.parseString(pubDepsOutput).getAsJsonObject();
        JsonElement elem = jsonObject.get("packages");
        return elem.getAsJsonArray();
    }

    private void generateDependencyGraph(DependencyGraph dependencyGraph, Dependency parentDependency, DartPubDep parent) {
        for (DartPubDep child: parent.dependencies) {
            Dependency dependency = Dependency.FACTORY.createNameVersionDependency(Forge.DART, child.name, child.version);
            if (parentDependency == null) {
                dependencyGraph.addDirectDependency(dependency);
            } else {
                dependencyGraph.addChildWithParent(dependency, parentDependency);
            }
            if (child.dependencies != null && child.dependencies.size() > 0) {
                generateDependencyGraph(dependencyGraph, dependency, child);
            }
        }
    }
}
