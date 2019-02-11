package com.synopsys.integration.detectable.detectables.hex.functional;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.hex.Rebar3TreeParser;
import com.synopsys.integration.detectable.detectables.hex.RebarParseResult;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class RebarParserFunctionalTest {
    private static Rebar3TreeParser rebar3TreeParser;
    private static ExternalIdFactory externalIdFactory;

    @BeforeAll
    static void setup() {
        externalIdFactory = new ExternalIdFactory();
        rebar3TreeParser = new Rebar3TreeParser(externalIdFactory);
    }

    @Test
    void testParseRebarTreeOutput() {
        final MutableMapDependencyGraph expectedGraph = new MutableMapDependencyGraph();
        final Dependency gitInnerParentDependency = buildDependency("git_inner_parent_dependency", "0.0.2");
        final Dependency hexInnerChildDependency = buildDependency("hex_inner_child_dependency", "0.3.0");
        final Dependency hexGrandchildDependency = buildDependency("hex_grandchild_dependency", "4.0.0");
        final Dependency gitInnerChildDependency = buildDependency("git_inner_child_dependency", "0.5.0");
        final Dependency gitGrandchildDependency = buildDependency("git_grandchild_dependency", "6.0.0");
        final Dependency gitOuterParentDependency = buildDependency("git_outer_parent_dependency", "0.0.7");
        final Dependency gitOuterChildDependency = buildDependency("git_outer_child_dependency", "0.8.0");

        expectedGraph.addChildrenToRoot(gitInnerParentDependency, gitOuterParentDependency);
        expectedGraph.addChildWithParent(hexInnerChildDependency, gitInnerParentDependency);
        expectedGraph.addChildWithParents(hexGrandchildDependency, hexInnerChildDependency);

        expectedGraph.addChildWithParent(gitInnerChildDependency, gitInnerParentDependency);
        expectedGraph.addChildWithParents(gitGrandchildDependency, gitInnerChildDependency);

        expectedGraph.addChildWithParents(gitOuterChildDependency, gitOuterParentDependency);

        final List<String> dependencyTreeOutput = FunctionalTestFiles.asListOfStrings("/hex/dependency-tree.txt");
        final RebarParseResult result = rebar3TreeParser.parseRebarTreeOutput(dependencyTreeOutput, "");
        final CodeLocation codeLocation = result.getCodeLocation();
        final DependencyGraph actualGraph = codeLocation.getDependencyGraph();

        GraphCompare.assertEquals(expectedGraph, actualGraph);
    }

    private Dependency buildDependency(final String name, final String version) {
        return new Dependency(name, version, externalIdFactory.createNameVersionExternalId(Forge.HEX, name, version));
    }
}
