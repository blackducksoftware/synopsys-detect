package com.synopsys.integration.detectable.detectables.conan.cli.graph;

import java.util.List;

public class ConanGraphNode {
    private final String ref; // conanfile.txt or package/version[@user/channel]
    private final String recipeRevision;
    private final String packageId;
    private final String packageRevision;
    private final List<String> requiresRefs;
    private final List<String> buildRequiresRefs;
    private final List<String> requiredByRefs;

    public ConanGraphNode(String ref, String recipeRevision, String packageId, String packageRevision, List<String> requiresRefs, List<String> buildRequiresRefs, List<String> requiredByRefs) {
        this.ref = ref;
        this.recipeRevision = recipeRevision;
        this.packageId = packageId;
        this.packageRevision = packageRevision;
        this.requiresRefs = requiresRefs;
        this.buildRequiresRefs = buildRequiresRefs;
        this.requiredByRefs = requiredByRefs;
    }

    public String getRef() {
        return ref;
    }

    public String getRecipeRevision() {
        return recipeRevision;
    }

    public String getPackageId() {
        return packageId;
    }

    public String getPackageRevision() {
        return packageRevision;
    }

    public List<String> getRequiresRefs() {
        return requiresRefs;
    }

    public List<String> getBuildRequiresRefs() {
        return buildRequiresRefs;
    }

    public List<String> getRequiredByRefs() {
        return requiredByRefs;
    }
}
