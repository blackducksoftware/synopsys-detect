package com.synopsys.integration.detectable.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.dependency.Dependency;

public class RootPruningGraphUtil {
    public static class CycleDetectedException extends Exception {
        public CycleDetectedException(String s) {
            super(s);
        }
    }

    //Given a Graph with root dependencies, returns a new graph where the only root dependencies are root dependencies not found elsewhere in the graph.
    //IE given [Root1 -> Child -> Root2, Root2] returns [Root1 -> Child -> Root2] where Root2 is no longer a root.
    public MutableDependencyGraph prune(DependencyGraph original) throws CycleDetectedException {
        MutableDependencyGraph destination = new MutableMapDependencyGraph();
        for (Dependency rootDependency : original.getRootDependencies()) {
            if (!isDependencyInGraph(rootDependency, original.getRootDependencies(), original)) {
                destination.addChildToRoot(rootDependency);
            }
            copyDescendants(rootDependency, singletonSet(rootDependency), destination, original);
        }
        return destination;
    }

    private void copyDescendants(Dependency parent, Set<Dependency> ancestors, MutableDependencyGraph destination, DependencyGraph original) throws CycleDetectedException {
        Set<Dependency> children = original.getChildrenForParent(parent);
        for (Dependency child : children) {
            destination.addParentWithChild(parent, child);
            if (!ancestors.contains(child)) {
                HashSet<Dependency> newAncestors = new HashSet<>(ancestors);
                newAncestors.add(child);
                copyDescendants(child, newAncestors, destination, original);
            } else {
                throw new CycleDetectedException("A cycle was detected with dependency " + child.toString() + ", this is not supported. Please contact support. ");
            }
        }
    }

    private boolean isDependencyInGraph(Dependency target, Set<Dependency> currentLevel, DependencyGraph graph) { //TODO: Should this method also detect cycles? The cycle test does not trigger it.
        for (Dependency currentLevelDependency : currentLevel) {
            Set<Dependency> children = graph.getChildrenForParent(currentLevelDependency);
            if (children.contains(target)) {
                return true;
            } else if (isDependencyInGraph(target, children, graph)) {
                return true;
            }
        }

        return false;
    }

    private <T> HashSet<T> singletonSet(T element) {
        return new HashSet<>(Collections.singletonList(element));
    }
}
