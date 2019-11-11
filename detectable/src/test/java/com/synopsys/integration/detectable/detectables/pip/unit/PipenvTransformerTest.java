package com.synopsys.integration.detectable.detectables.pip.unit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectable.util.DetectableStringUtils;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pip.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvResult;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvGraphParser;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvTransformer;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@UnitTest
public class PipenvTransformerTest {
    @Test
    void resolvesFuzzyVersion() {
        List<PipFreezeEntry> pipFreezeEntries = new ArrayList<>();
        pipFreezeEntries.add(new PipFreezeEntry("example", "2.0.0"));
        PipFreeze pipFreeze = new PipFreeze(pipFreezeEntries);

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        pipenvGraphEntries.add(new PipenvGraphEntry("example", "fuzzy", new ArrayList<>()));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        PipenvResult result = pipenvTransformer.transform("", "", pipFreeze, pipenvGraph);
        DependencyGraph graph = result.getCodeLocation().getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasDependency("example", "2.0.0");
    }

    @Test
    void resolvesLowercaseNameWithFreezeCapital() {
        List<PipFreezeEntry> pipFreezeEntries = new ArrayList<>();
        pipFreezeEntries.add(new PipFreezeEntry("Example", "2.0.0"));
        PipFreeze pipFreeze = new PipFreeze(pipFreezeEntries);

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        pipenvGraphEntries.add(new PipenvGraphEntry("example", "fuzzy", new ArrayList<>()));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        PipenvResult result = pipenvTransformer.transform("", "", pipFreeze, pipenvGraph);
        DependencyGraph graph = result.getCodeLocation().getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasDependency("Example", "2.0.0");
    }

    @Test
    void usesProjectDependencyAsRoot() {
        PipFreeze pipFreeze = new PipFreeze(new ArrayList<>());

        List<PipenvGraphEntry> pipenvGraphEntries = new ArrayList<>();
        List<PipenvGraphDependency> children = new ArrayList<>();
        children.add(new PipenvGraphDependency("shouldBeAtRoot", "shouldbeAtRootVersion", new ArrayList<>()));
        pipenvGraphEntries.add(new PipenvGraphEntry("projectName", "projectVersion", children));
        PipenvGraph pipenvGraph = new PipenvGraph(pipenvGraphEntries);

        PipenvTransformer pipenvTransformer = new PipenvTransformer(new ExternalIdFactory());
        PipenvResult result = pipenvTransformer.transform("projectName", "projectVersion", pipFreeze, pipenvGraph);
        DependencyGraph graph = result.getCodeLocation().getDependencyGraph();

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.PYPI, graph);
        graphAssert.hasRootDependency("shouldBeAtRoot", "shouldbeAtRootVersion");
    }
}
