package com.synopsys.integration.detectable.detectables.pip.unit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry;
import com.synopsys.integration.detectable.detectables.pip.parser.PipenvGraphParser;

@UnitTest
public class PipenvGraphParserTests {
    @Test
    void parsesEntries() {
        List<String> pipGraphText = new ArrayList<>();
        pipGraphText.add("entry-one==1");
        pipGraphText.add("entry-two==2.0");

        final PipenvGraphParser pipenvGraphParser = new PipenvGraphParser();
        final PipenvGraph pipenvGraph = pipenvGraphParser.parse(pipGraphText);

        Assertions.assertEquals(2, pipenvGraph.getEntries().size(), "Pip graph should have created 2 entries.");
        assertContainsEntry("entry-one", "1", pipenvGraph);
        assertContainsEntry("entry-two", "2.0", pipenvGraph);
    }

    @Test
    void parsesSimpleHeirarchy() {
        List<String> pipGraphText = new ArrayList<>();
        pipGraphText.add("entry==1");
        pipGraphText.add("  - dependency-parent [required: Any, installed: 1.0.0]");
        pipGraphText.add("    - dependency-child [required: Any, installed: 2.0.0]");

        final PipenvGraphParser pipenvGraphParser = new PipenvGraphParser();
        final PipenvGraph pipenvGraph = pipenvGraphParser.parse(pipGraphText);

        PipenvGraphEntry entry = pipenvGraph.getEntries().get(0);
        Assertions.assertEquals("entry", entry.getName());
        Assertions.assertEquals("1", entry.getVersion());

        PipenvGraphDependency parentDep = entry.getChildren().get(0);
        Assertions.assertEquals("dependency-parent", parentDep.getName());
        Assertions.assertEquals("1.0.0", parentDep.getInstalledVersion());

        PipenvGraphDependency childDep = parentDep.getChildren().get(0);
        Assertions.assertEquals("dependency-child", childDep.getName());
        Assertions.assertEquals("2.0.0", childDep.getInstalledVersion());
    }

    @Test
    void parsesComplexRequires() {
        List<String> pipGraphText = new ArrayList<>();
        pipGraphText.add("entry==1");
        pipGraphText.add("  - dependency-parent [required: >=2.0.1,!=2.1.6,!=2.1.2,!=2.0.4, installed: 1.0.0]");

        final PipenvGraphParser pipenvGraphParser = new PipenvGraphParser();
        final PipenvGraph pipenvGraph = pipenvGraphParser.parse(pipGraphText);

        PipenvGraphDependency parentDep = pipenvGraph.getEntries().get(0).getChildren().get(0);
        Assertions.assertEquals("dependency-parent", parentDep.getName());
        Assertions.assertEquals("1.0.0", parentDep.getInstalledVersion());
    }

    @Test
    void parsesComplexHeirarchy() {
        List<String> pipGraphText = new ArrayList<>();
        pipGraphText.add("entry1==1");
        pipGraphText.add("  - dependency-parent1 [required: Any, installed: 1.0.0]");
        pipGraphText.add("    - dependency-child1 [required: Any, installed: 1.1.0]");
        pipGraphText.add("      - dependency-grand-child1 [required: Any, installed: 1.1.1]");
        pipGraphText.add("      - dependency-grand-child2 [required: Any, installed: 1.1.2]");
        pipGraphText.add("  - dependency-parent2 [required: Any, installed: 2.0.0]");
        pipGraphText.add("    - dependency-child2 [required: Any, installed: 2.1.0]");
        pipGraphText.add("entry2==2");
        pipGraphText.add("  - dependency-parent3 [required: Any, installed: 3.0.0]");
        pipGraphText.add("    - dependency-child3 [required: Any, installed: 3.1.0]");
        pipGraphText.add("entry3==3");
        pipGraphText.add("  - dependency-parent4 [required: Any, installed: 4.0.0]");

        final PipenvGraphParser pipenvGraphParser = new PipenvGraphParser();
        final PipenvGraph pipenvGraph = pipenvGraphParser.parse(pipGraphText);

        Assertions.assertEquals(3, pipenvGraph.getEntries().size(), "Pip graph should have created 3 entries.");

        assertDependencyPath(pipenvGraph, "1.0.0", "entry1", "dependency-parent1");
        assertDependencyPath(pipenvGraph, "1.1.0", "entry1", "dependency-parent1", "dependency-child1");
        assertDependencyPath(pipenvGraph, "1.1.1", "entry1", "dependency-parent1", "dependency-child1", "dependency-grand-child1");
        assertDependencyPath(pipenvGraph, "1.1.2", "entry1", "dependency-parent1", "dependency-child1", "dependency-grand-child2");

        assertDependencyPath(pipenvGraph, "2.0.0", "entry1", "dependency-parent2");
        assertDependencyPath(pipenvGraph, "2.1.0", "entry1", "dependency-parent2", "dependency-child2");

        assertDependencyPath(pipenvGraph, "3.0.0", "entry2", "dependency-parent3");
        assertDependencyPath(pipenvGraph, "3.1.0", "entry2", "dependency-parent3", "dependency-child3");

        assertDependencyPath(pipenvGraph, "4.0.0", "entry3", "dependency-parent4");
    }

    private void assertDependencyPath(PipenvGraph pipenvGraph, String version, String... path) {
        Optional<PipenvGraphEntry> entry = pipenvGraph.getEntries().stream()
                                     .filter(it->it.getName().equals(path[0]))
                                     .findFirst();
        Assertions.assertTrue(entry.isPresent(), String.format("Could not find entry with name '%s' in pip graph.", path[0]));
        Optional<PipenvGraphDependency> dependency = entry.get().getChildren().stream()
                                               .filter(it -> it.getName().equals(path[1]))
                                               .findFirst();
        Assertions.assertTrue(dependency.isPresent(), String.format("Could not find dependency with name '%s' in pip graph.", path[1]));
        for (int i = 2; i < path.length; i++) {
            String currentPath = path[i];
            dependency = dependency.get().getChildren().stream()
                             .filter(it -> it.getName().equals(currentPath))
                             .findFirst();
            Assertions.assertTrue(dependency.isPresent(), String.format("Could not find dependency with name '%s' in pip graph.", currentPath));
        }

        Assertions.assertEquals(dependency.get().getInstalledVersion(), version, "The dependency found at the path '"+ Arrays.toString(path) + "' did not match the expected version: " + version);
    }

    private PipenvGraphEntry assertContainsEntry(String name, String version, PipenvGraph pipenvGraph) {
        Optional<PipenvGraphEntry> found = pipenvGraph.getEntries().stream()
            .filter(it->it.getName().equals(name))
            .filter(it->it.getVersion().equals(version))
            .findFirst();

        Assertions.assertTrue(found.isPresent(), String.format("Could not find pip freeze entry with name '%s' and version '%s'", name, version));

        return found.get();
    }
}
