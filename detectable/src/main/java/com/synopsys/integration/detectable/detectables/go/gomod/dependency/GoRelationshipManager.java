package com.synopsys.integration.detectable.detectables.go.gomod.dependency;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.synopsys.integration.detectable.detectables.go.gomod.model.GoGraphRelationship;

public class GoRelationshipManager {
    private final Map<String, List<GoGraphRelationship>> relationshipMap;
    private final Set<String> excludedModules;

    public GoRelationshipManager(List<GoGraphRelationship> goGraphRelationships, Set<String> excludedModules) {
        this.excludedModules = excludedModules;
        relationshipMap = new HashMap<>();
        for (GoGraphRelationship goGraphRelationship : goGraphRelationships) {
            String parentName = goGraphRelationship.getParent().getName();

            if (relationshipMap.containsKey(parentName)) {
                relationshipMap.get(parentName).add(goGraphRelationship);
            } else {
                List<GoGraphRelationship> relationships = new LinkedList<>();
                relationships.add(goGraphRelationship);
                relationshipMap.put(parentName, relationships);
            }
        }
    }

    public boolean hasRelationshipsFor(String moduleName) {
        return relationshipMap.containsKey(moduleName);
    }

    public List<GoGraphRelationship> getRelationshipsFor(String moduleName) {
        return relationshipMap.get(moduleName);
    }

    public boolean isNotUsedByMainModule(String moduleName) {
        return excludedModules.contains(moduleName);
    }
}
