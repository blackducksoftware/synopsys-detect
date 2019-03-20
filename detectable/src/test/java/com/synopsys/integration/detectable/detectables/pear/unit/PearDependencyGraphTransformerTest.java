package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@UnitTest
class PearDependencyGraphTransformerTest {
    @Test
    void buildDependencyGraphRequiredOnly() {
        final DependencyGraph dependencyGraph = buildDependencyGraph(true);

        final Set<Dependency> rootDependencies = dependencyGraph.getRootDependencies();
        Assert.assertEquals(1, rootDependencies.size());

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final GraphAssert graphAssert = new GraphAssert(Forge.PEAR, dependencyGraph);
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.PEAR, "Archive_Tar", "1.4.3"));
    }

    @Test
    void buildDependencyGraphAll() {
        final DependencyGraph dependencyGraph = buildDependencyGraph(false);

        final Set<Dependency> rootDependencies = dependencyGraph.getRootDependencies();
        Assert.assertEquals(2, rootDependencies.size());

        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final GraphAssert graphAssert = new GraphAssert(Forge.PEAR, dependencyGraph);
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.PEAR, "Archive_Tar", "1.4.3"));
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.PEAR, "Console_Getopt", "1.4.1"));
    }

    private DependencyGraph buildDependencyGraph(final boolean onlyGatherRequired) {
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final PearDependencyGraphTransformer pearDependencyGraphTransformer = new PearDependencyGraphTransformer(externalIdFactory);

        final Map<String, String> nameVersionMap = new HashMap<>();
        nameVersionMap.put("Archive_Tar", "1.4.3");
        nameVersionMap.put("Console_Getopt", "1.4.1");

        final List<PackageDependency> packageDependencies = new ArrayList<>();
        packageDependencies.add(new PackageDependency("Archive_Tar", true));
        packageDependencies.add(new PackageDependency("Console_Getopt", false));

        return pearDependencyGraphTransformer.buildDependencyGraph(nameVersionMap, packageDependencies, onlyGatherRequired);
    }
}