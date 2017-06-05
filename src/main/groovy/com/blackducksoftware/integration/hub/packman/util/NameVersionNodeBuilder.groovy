package com.blackducksoftware.integration.hub.packman.util

/**
 * This class should be used to construct node graphs where you don't
 * always have a defined version for each dependency, but will EVENTUALLY find
 * a defined version, as in Gemfile.lock files.
 */
class NameVersionNodeBuilder {
    final Map<String, NameVersionNode> nameToNodeMap = [:]

    final NameVersionNode root

    public NameVersionNodeBuilder(final NameVersionNode root) {
        this.root = root
        nameToNodeMap.put(root.name, root)
    }

    public void addChildNodeToParent(final NameVersionNode child, final NameVersionNode parent) {
        if (!nameToNodeMap.containsKey(child.name)) {
            nameToNodeMap.put(child.name, child)
        }

        if (!nameToNodeMap.containsKey(parent.name)) {
            nameToNodeMap.put(parent.name, parent)
        }

        if (child.version?.trim() && !nameToNodeMap[child.name].version?.trim()) {
            nameToNodeMap[child.name].version = child.version
        }

        if (parent.version?.trim() && !nameToNodeMap[parent.name].version?.trim()) {
            nameToNodeMap[parent.name].version = parent.version
        }

        nameToNodeMap[parent.name].children.add(nameToNodeMap[child.name])
    }
}
