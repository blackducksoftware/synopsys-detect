package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pear.PearDependencyType;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.transform.PearDependencyGraphTransformer;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@UnitTest
class PearDependencyGraphTransformerTest {
    @Test
    void buildDependencyGraphRequiredOnly() {
        DependencyGraph dependencyGraph = buildDependencyGraph(PearDependencyType.OPTIONAL);

        Set<Dependency> rootDependencies = dependencyGraph.getRootDependencies();
        Assertions.assertEquals(1, rootDependencies.size());

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        GraphAssert graphAssert = new GraphAssert(Forge.PEAR, dependencyGraph);
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.PEAR, "Archive_Tar", "1.4.3"));
    }

    @Test
    void buildDependencyGraphAll() {
        DependencyGraph dependencyGraph = buildDependencyGraph();

        Set<Dependency> rootDependencies = dependencyGraph.getRootDependencies();
        Assertions.assertEquals(2, rootDependencies.size());

        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        GraphAssert graphAssert = new GraphAssert(Forge.PEAR, dependencyGraph);
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.PEAR, "Archive_Tar", "1.4.3"));
        graphAssert.hasDependency(externalIdFactory.createNameVersionExternalId(Forge.PEAR, "Console_Getopt", "1.4.1"));
    }

    private DependencyGraph buildDependencyGraph(PearDependencyType... excludedTypes) {
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        PearDependencyGraphTransformer pearDependencyGraphTransformer = new PearDependencyGraphTransformer(externalIdFactory, EnumListFilter.fromExcluded(excludedTypes));

        Map<String, String> nameVersionMap = new HashMap<>();
        nameVersionMap.put("Archive_Tar", "1.4.3");
        nameVersionMap.put("Console_Getopt", "1.4.1");

        List<PackageDependency> packageDependencies = new ArrayList<>();
        packageDependencies.add(new PackageDependency("Archive_Tar", true));
        packageDependencies.add(new PackageDependency("Console_Getopt", false));

        return pearDependencyGraphTransformer.buildDependencyGraph(nameVersionMap, packageDependencies);
    }
}