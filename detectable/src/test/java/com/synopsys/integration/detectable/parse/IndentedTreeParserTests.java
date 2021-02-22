package com.synopsys.integration.detectable.parse;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.parse.IndentedTreeParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

public class IndentedTreeParserTests {
    IndentedTreeParser<String> parser = new IndentedTreeParser<>();
    ExternalIdFactory factory = new ExternalIdFactory();

    private int levelFromLine(String line) {
        return StringUtils.countMatches(line, "  ");
    }

    private Dependency dependencyFromLine(String line) {
        line = line.trim();
        return new Dependency(line, line, factory.createNameVersionExternalId(Forge.ALPINE, line, line));
    }

    private NameVersionGraphAssert assertFirstGraph(String... inputs) {
        List<DependencyGraph> trees = parser.parseTrees(Arrays.asList(inputs), this::levelFromLine, this::dependencyFromLine);

        Assertions.assertEquals(1, trees.size());
        return new NameVersionGraphAssert(Forge.ALPINE, trees.get(0));
    }

    @Test
    public void simpleTree() {
        NameVersionGraphAssert graphAssert = assertFirstGraph("parent", "  child", "    grandchild");
        graphAssert.hasRootDependency("parent", "parent");
        graphAssert.hasParentChildRelationship("parent", "parent", "child", "child");
        graphAssert.hasParentChildRelationship("child", "child", "grandchild", "grandchild");
    }

    @Test
    public void twoChildren() {
        NameVersionGraphAssert graphAssert = assertFirstGraph("parent", "  child1", "  child2", "    grandchild");
        graphAssert.hasRootDependency("parent", "parent");
        graphAssert.hasParentChildRelationship("parent", "parent", "child1", "child1");
        graphAssert.hasParentChildRelationship("parent", "parent", "child2", "child2");
        graphAssert.hasParentChildRelationship("child2", "child2", "grandchild", "grandchild");
    }
}
