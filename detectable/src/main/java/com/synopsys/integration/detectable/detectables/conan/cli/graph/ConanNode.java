package com.synopsys.integration.detectable.detectables.conan.cli.graph;

import java.util.List;

public class ConanNode {
    // if rootNode: conanfile.{txt,py}[ (projectname/version)]
    // else       : package/version[@user/channel]
    private final String ref;
    private final String filename; // conanfile.txt, conanfile.py
    private final String name;
    private final String version;
    private final String user;
    private final String channel;

    private final String recipeRevision;
    private final String packageId;
    private final String packageRevision;
    private final List<String> requiresRefs;
    private final List<String> buildRequiresRefs;
    private final List<String> requiredByRefs;
    private final boolean rootNode;

    public ConanNode(String ref, String filename, String name, String version, String user, String channel,
        String recipeRevision, String packageId, String packageRevision, List<String> requiresRefs, List<String> buildRequiresRefs,
        List<String> requiredByRefs, boolean rootNode) {
        this.ref = ref;
        this.filename = filename;
        this.name = name;
        this.version = version;
        this.user = user;
        this.channel = channel;
        this.recipeRevision = recipeRevision;
        this.packageId = packageId;
        this.packageRevision = packageRevision;
        this.requiresRefs = requiresRefs;
        this.buildRequiresRefs = buildRequiresRefs;
        this.requiredByRefs = requiredByRefs;
        this.rootNode = rootNode;
    }

    public String getRef() {
        return ref;
    }

    public String getFilename() {
        return filename;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public String getUser() {
        return user;
    }

    public String getChannel() {
        return channel;
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

    public boolean isRootNode() {
        return rootNode;
    }
}
