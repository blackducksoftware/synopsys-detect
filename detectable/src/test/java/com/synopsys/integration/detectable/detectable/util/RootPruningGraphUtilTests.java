package com.synopsys.integration.detectable.detectable.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.MutableDependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.dependency.DependencyFactory;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.detectable.util.RootPruningGraphUtil;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class RootPruningGraphUtilTests {

    DependencyFactory dependencyFactory = new DependencyFactory(new ExternalIdFactory());
    Forge anyForge = Forge.MAVEN;

    @Test
    public void simpleTwoRootIsPruned() throws CycleDetectedException {
        Dependency root1 = dependencyFactory.createNameVersionDependency(anyForge, "root1", "version");
        Dependency root2 = dependencyFactory.createNameVersionDependency(anyForge, "root2", "version");
        Dependency child = dependencyFactory.createNameVersionDependency(anyForge, "child", "version");

        MutableMapDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addChildrenToRoot(root1, root2);
        graph.addParentWithChild(root1, child);
        graph.addParentWithChild(child, root2);

        MutableDependencyGraph prunedGraph = new RootPruningGraphUtil().prune(graph);
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(anyForge, prunedGraph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("root1", "version");
        graphAssert.hasParentChildRelationship("root1", "version", "child", "version");
        graphAssert.hasParentChildRelationship("child", "version", "root2", "version");
    }

    @Test()
    public void cyclicalGraphPruned() {
        Dependency parent = dependencyFactory.createNameVersionDependency(anyForge, "parent", "version");
        Dependency child = dependencyFactory.createNameVersionDependency(anyForge, "child", "version");

        MutableMapDependencyGraph graph = new MutableMapDependencyGraph();
        graph.addChildrenToRoot(parent);
        graph.addParentWithChild(parent, child);
        graph.addParentWithChild(child, parent);

        Assertions.assertThrows(CycleDetectedException.class, () -> {
            MutableDependencyGraph prunedGraph = new RootPruningGraphUtil().prune(graph);
        });

        /*
        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(anyForge, prunedGraph);
        graphAssert.hasRootSize(1);
        graphAssert.hasRootDependency("parent", "version");
        graphAssert.hasParentChildRelationship("parent", "version", "child", "version");
        graphAssert.hasParentChildRelationship("child", "version", "parent", "version");
        */
    }

}
