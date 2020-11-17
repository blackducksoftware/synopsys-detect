package com.synopsys.integration.detectable.detectables.conan.cli.graph;

import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConanGraphNodeBuilder {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private String ref;
    private String filename;
    private String name;
    private String version;
    private String user;
    private String channel;
    private String recipeRevision;
    private String packageId;
    private String packageRevision;
    private List<String> requiresRefs;
    private List<String> buildRequiresRefs;
    private List<String> requiredByRefs;

    public ConanGraphNodeBuilder setRef(String ref) {
        ref = ref.trim();
        this.ref = ref;
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

    public Optional<ConanGraphNode> build() {
        if (StringUtils.isBlank(ref) || StringUtils.isBlank(packageId)) {
            logger.debug("This wasn't a node");
            return Optional.empty();
        }
        // if rootNode: conanfile.{txt,py}[ (projectname/version)]
        // else       : package/version[@user/channel]
        if (ref.startsWith("conanfile.")) {
            StringTokenizer tokenizer = new StringTokenizer(ref, " \t()/");
            filename = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                name = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    version = tokenizer.nextToken();
                }
            }
            logger.info(String.format("filename: %s; name: %s; version: %s", filename, name, version));
        } else {
            StringTokenizer tokenizer = new StringTokenizer(ref, "/@");
            name = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                version = tokenizer.nextToken();
                if (tokenizer.hasMoreTokens()) {
                    user = tokenizer.nextToken();
                    if (tokenizer.hasMoreTokens()) {
                        channel = tokenizer.nextToken();
                    }
                }
            }
            logger.info(String.format("name: %s; version: %s; user: %s; channel: %s", name, version, user, channel));
        }
        boolean isRootNode = false;
        if ((filename != null) && CollectionUtils.isEmpty(requiredByRefs)) {
            isRootNode = true;
        } else if (CollectionUtils.isEmpty(requiredByRefs)) {
            logger.warn(String.format("Node %s doesn't look like a root node, but its requiredBy list is empty; treating it as a non-root node", ref));
            // TODO this may need to change after requiredBy parsing implemented
            isRootNode = false;
        } else {
            isRootNode = false;
        }
        ConanGraphNode node = new ConanGraphNode(ref, filename, name, version, user, channel,
            recipeRevision, packageId, packageRevision, requiresRefs, buildRequiresRefs, requiredByRefs, isRootNode);
        return Optional.of(node);
    }
}
