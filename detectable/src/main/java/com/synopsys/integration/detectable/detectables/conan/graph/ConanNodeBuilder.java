package com.synopsys.integration.detectable.detectables.conan.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConanNodeBuilder<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String ref;
    private String path;
    private String name;
    private String version;
    private String user;
    private String channel;
    private String recipeRevision;
    private String packageId;
    private String packageRevision;
    private final List<T> requiresRefs = new ArrayList<>();
    private final List<T> buildRequiresRefs = new ArrayList<>();
    private boolean valid = true;
    private boolean forcedRootNode = false;

    public ConanNodeBuilder() {}

    public ConanNodeBuilder(ConanNode<Integer> initializingNode) {
        this.forcedRootNode = initializingNode.isRootNode();
        this.ref = initializingNode.getRef();
        this.path = initializingNode.getPath().orElse(null);
        this.name = initializingNode.getName().orElse(null);
        this.version = initializingNode.getVersion().orElse(null);
        this.user = initializingNode.getUser().orElse(null);
        this.channel = initializingNode.getChannel().orElse(null);
        this.recipeRevision = initializingNode.getRecipeRevision().orElse(null);
        this.packageId = initializingNode.getPackageId().orElse(null);
        this.packageRevision = initializingNode.getPackageRevision().orElse(null);
    }

    public ConanNodeBuilder<T> forceRootNode() {
        forcedRootNode = true;
        return this;
    }

    public ConanNodeBuilder<T> setRef(String ref) {
        if (ref != null) {
            this.ref = ref.trim();
        }
        return this;
    }

    public ConanNodeBuilder<T> setPath(String path) {
        if (path != null) {
            this.path = path.trim();
        }
        return this;
    }

    public ConanNodeBuilder<T> setName(String name) {
        if (name != null) {
            this.name = name.trim();
        }
        return this;
    }

    public ConanNodeBuilder<T> setVersion(String version) {
        if (version != null) {
            this.version = version.trim();
        }
        return this;
    }

    public ConanNodeBuilder<T> setUser(String user) {
        if (user != null) {
            this.user = user.trim();
        }
        return this;
    }

    public ConanNodeBuilder<T> setChannel(String channel) {
        if (channel != null) {
            this.channel = channel.trim();
        }
        return this;
    }

    public ConanNodeBuilder<T> setRecipeRevision(String recipeRevision) {
        this.recipeRevision = recipeRevision;
        return this;
    }

    public ConanNodeBuilder<T> setPackageId(String packageId) {
        this.packageId = packageId;
        return this;
    }

    public ConanNodeBuilder<T> setPackageRevision(String packageRevision) {
        this.packageRevision = packageRevision;
        return this;
    }

    public ConanNodeBuilder<T> addRequiresRef(T requiresRef) {
        this.requiresRefs.add(requiresRef);
        return this;
    }

    public ConanNodeBuilder<T> addBuildRequiresRef(T buildRequiresRef) {
        this.buildRequiresRefs.add(buildRequiresRef);
        return this;
    }

    public ConanNodeBuilder<T> setValid(boolean valid) {
        this.valid = valid;
        return this;
    }

    public Optional<ConanNode<T>> build() {
        if (StringUtils.isBlank(ref) && StringUtils.isBlank(path)) {
            valid = false;
        }
        if (!valid) {
            logger.debug("This wasn't a node");
            return Optional.empty();
        }
        if (StringUtils.isBlank(ref) && StringUtils.isNotBlank(path)) {
            ref = path;
        }
        boolean isRootNode = false;
        if (forcedRootNode || (path != null)) {
            isRootNode = true;
        }
        ConanNode<T> node = new ConanNode<>(
            ref,
            path,
            name,
            version,
            user,
            channel,
            recipeRevision,
            packageId,
            packageRevision,
            requiresRefs,
            buildRequiresRefs,
            isRootNode
        );
        logger.trace("node: {}", node);
        return Optional.of(node);
    }
}
