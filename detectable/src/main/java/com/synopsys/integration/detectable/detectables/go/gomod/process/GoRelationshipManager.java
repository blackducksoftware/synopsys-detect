package com.synopsys.integration.detectable.detectables.go.gomod.process;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
            relationshipMap.putIfAbsent(parentName, new LinkedList<>());
            relationshipMap.get(parentName).add(goGraphRelationship);
        }
    }

    public boolean hasRelationshipsFor(String moduleName) {
        return relationshipMap.containsKey(moduleName);
    }

    public List<GoGraphRelationship> getRelationshipsFor(String moduleName) {
        return Optional.ofNullable(relationshipMap.get(moduleName)).orElse(Collections.emptyList());
    }

    public boolean isNotUsedByMainModule(String moduleName) {
        return excludedModules.contains(moduleName);
    }
}
