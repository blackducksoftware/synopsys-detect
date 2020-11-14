package com.synopsys.integration.detectable.detectables.conan.cli.graph;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ConanGraphNodeBuilder {
    private String ref;
    private String recipeRevision;
    private String packageId;
    private String packageRevision;
    private List<String> requiresRefs;
    private List<String> buildRequiresRefs;
    private List<String> requiredByRefs;

    public ConanGraphNodeBuilder setRef(String ref) {
        this.ref = ref;
        // TODO TEMP
        if (ref.matches("[^(/]+ ([^/]+/[^/]+)$")) {
            System.out.printf("Node ref '%s' has name (name/version)\n", ref);
        } else if (ref.matches("[^/ ]+/[^/ ]+$")) {
            System.out.printf("Node ref '%s' has pkg name/version\n", ref);
        } else {
            System.out.printf("Node ref '%s' has no useful information\n", ref);
        }
        return this;
    }

    public ConanGraphNodeBuilder setRecipeRevision(String recipeRevision) {
        this.recipeRevision = recipeRevision;
        return this;
    }

    public ConanGraphNodeBuilder setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public ConanGraphNodeBuilder setPackageRevision(String packageRevision) {
        this.packageRevision = packageRevision;
        return this;
    }

    public ConanGraphNodeBuilder setRequiresRefs(List<String> requiresRefs) {
        this.requiresRefs = requiresRefs;
        return this;
    }

    public ConanGraphNodeBuilder setBuildRequiresRefs(List<String> buildRequiresRefs) {
        this.buildRequiresRefs = buildRequiresRefs;
        return this;
    }

    public ConanGraphNodeBuilder setRequiredByRefs(List<String> requiredByRefs) {
        this.requiredByRefs = requiredByRefs;
        return this;
    }

    public ConanGraphNode build() {
        if (StringUtils.isBlank(ref)) {
            throw new UnsupportedOperationException("ConanGraphNodeBuilder prerequisites have not been met");
        }
        return new ConanGraphNode(ref, recipeRevision, packageId, packageRevision, requiresRefs, buildRequiresRefs, requiredByRefs);
    }
}
