package com.synopsys.integration.detectable.detectables.conan.graph;

import java.util.List;
import java.util.Optional;

import com.synopsys.integration.util.Stringable;

public class ConanNode<T> extends Stringable {
    // if rootNode: conanfile.{txt,py}[ (projectname/version)]
    // else       : package/version[@user/channel]
    private final String ref;
    private final String path; // conanfile.txt, conanfile.py
    private final String name;
    private final String version;
    private final String user;
    private final String channel;

    private final String recipeRevision;
    private final String packageId;
    private final String packageRevision;
    private final List<T> requiresRefs;
    private final List<T> buildRequiresRefs;
    private final boolean rootNode;

    public ConanNode(
        String ref,
        String path,
        String name,
        String version,
        String user,
        String channel,
        String recipeRevision,
        String packageId,
        String packageRevision,
        List<T> requiresRefs,
        List<T> buildRequiresRefs,
        boolean rootNode
    ) {
        this.ref = ref;
        this.path = path;
        this.name = name;
        this.version = version;
        this.user = user;
        this.channel = channel;
        this.recipeRevision = recipeRevision;
        this.packageId = packageId;
        this.packageRevision = packageRevision;
        this.requiresRefs = requiresRefs;
        this.buildRequiresRefs = buildRequiresRefs;
        this.rootNode = rootNode;
    }

    public String getRef() {
        return ref;
    }

    public Optional<String> getPath() {
        return Optional.ofNullable(path);
    }

    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    public Optional<String> getVersion() {
        return Optional.ofNullable(version);
    }

    public Optional<String> getUser() {
        return Optional.ofNullable(user);
    }

    public Optional<String> getChannel() {
        return Optional.ofNullable(channel);
    }

    public Optional<String> getRecipeRevision() {
        return Optional.ofNullable(recipeRevision);
    }

    public Optional<String> getPackageId() {
        return Optional.ofNullable(packageId);
    }

    public Optional<String> getPackageRevision() {
        return Optional.ofNullable(packageRevision);
    }

    public Optional<List<T>> getRequiresRefs() {
        return Optional.ofNullable(requiresRefs);
    }

    public Optional<List<T>> getBuildRequiresRefs() {
        return Optional.ofNullable(buildRequiresRefs);
    }

    public boolean isRootNode() {
        return rootNode;
    }
}
