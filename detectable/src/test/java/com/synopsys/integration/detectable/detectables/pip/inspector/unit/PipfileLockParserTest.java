package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipenvDependencyType;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockParser;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLock;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependencyEntry;

public class PipfileLockParserTest {
    @Test
    public void testParse() {
        PipfileLock pipfileLock = pipfileLock();
        PipfileLockParser parser = new PipfileLockParser();
        List<PipfileLockDependency> dependencies = parser.parse(pipfileLock, EnumListFilter.excludeNone());

        Assertions.assertEquals(3, dependencies.size());
        Assertions.assertTrue(containsDependency(dependencies, "comp1", "1.0"));
        Assertions.assertTrue(containsDependency(dependencies, "comp2", "2.0"));
        Assertions.assertTrue(containsDependency(dependencies, "comp3", "3.0"));
    }

    private boolean containsDependency(List<PipfileLockDependency> pipfileLockDependencies, String name, String version) {
        return pipfileLockDependencies.stream()
            .anyMatch(dependency -> dependency.getName().equals(name) && dependency.getVersion().equals(version));
    }

    @Test
    public void testExcludeDevelopDependencies() {
        PipfileLock pipfileLock = pipfileLock();
        PipfileLockParser parser = new PipfileLockParser();
        List<PipfileLockDependency> dependencies = parser.parse(pipfileLock, EnumListFilter.fromExcluded(PipenvDependencyType.DEV));

        Assertions.assertEquals(2, dependencies.size());
        Assertions.assertTrue(containsDependency(dependencies, "comp1", "1.0"));
        Assertions.assertTrue(containsDependency(dependencies, "comp2", "2.0"));
        Assertions.assertFalse(containsDependency(dependencies, "comp3", "3.0"));
    }

    private PipfileLock pipfileLock() {
        PipfileLock pipfileLock = new PipfileLock();

        Map<String, PipfileLockDependencyEntry> dependencies = new HashMap<>();
        dependencies.put("comp1", createEntry("==1.0"));
        dependencies.put("comp2", createEntry("==2.0"));
        pipfileLock.dependencies = dependencies;

        Map<String, PipfileLockDependencyEntry> devDependencies = new HashMap<>();
        devDependencies.put("comp3", createEntry("==3.0"));
        pipfileLock.devDependencies = devDependencies;

        return pipfileLock;
    }

    private PipfileLockDependencyEntry createEntry(String version) {
        PipfileLockDependencyEntry entry = new PipfileLockDependencyEntry();
        entry.version = version;
        return entry;
    }
}
