package com.blackducksoftware.integration.hub.detect.testutils

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode

class DependencyNodeUtil {
    public static final Comparator<DependencyNode> DATA_ID_COMPARATOR = new Comparator<DependencyNode>() {
        public int compare(final DependencyNode lhs, final DependencyNode rhs) {
            if (lhs == rhs || lhs.externalId == rhs.externalId) {
                0
            } else if (lhs == null || lhs.externalId == null) {
                -1
            } else if (rhs == null || rhs.externalId == null) {
                1
            }

            lhs.externalId.createDataId().compareTo(rhs.externalId.createDataId())
        }
    }

    void buildNodeString(StringBuilder stringBuilder, int currentLevel, DependencyNode node) {
        String prefix = '  '.multiply(currentLevel)
        stringBuilder.append(prefix + node.externalId.createExternalId() + '\n')
        node.children.each {
            buildNodeString(stringBuilder, currentLevel + 1, it)
        }
    }

    void sortDependencyNode(DependencyNode dependencyNode) {
        if (!dependencyNode.children.isEmpty()) {
            TreeSet<DependencyNode> sortedChildren = new TreeSet<>(DATA_ID_COMPARATOR)
            sortedChildren.addAll(dependencyNode.children)

            dependencyNode.children = sortedChildren
            dependencyNode.children.each { sortDependencyNode(it) }
        }
    }
}
