package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.detectable.util.EnumListFilter;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipenvDependencyType;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockDependencyVersionParser;
import com.synopsys.integration.detectable.detectables.pipenv.parse.PipfileLockTransformer;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLock;
import com.synopsys.integration.detectable.detectables.pipenv.parse.data.PipfileLockDependencyEntry;
import com.synopsys.integration.detectable.detectables.pipenv.parse.model.PipfileLockDependency;

public class PipfileLockParserTest {
    @Test
    public void testParse() {
        PipfileLock pipfileLock = pipfileLock();
        PipfileLockDependencyVersionParser dependencyVersionParser = new PipfileLockDependencyVersionParser();
        PipfileLockTransformer parser = new PipfileLockTransformer(dependencyVersionParser, EnumListFilter.excludeNone());
        List<PipfileLockDependency> dependencies = parser.transform(pipfileLock);

        Assertions.assertEquals(3, dependencies.size());
        Assertions.assertTrue(containsDependency(dependencies, "comp1", "1.0"));
        Assertions.assertTrue(containsDependency(dependencies, "comp2", null));
        Assertions.assertTrue(containsDependency(dependencies, "comp3", "3.0"));
    }

    private boolean containsDependency(List<PipfileLockDependency> pipfileLockDependencies, String name, String version) {
        return pipfileLockDependencies.stream()
            .anyMatch(dependency ->
                dependency.getName().equals(name) &&
                    ((dependency.getVersion() == null && version == null) || dependency.getVersion().equals(version))
            );
    }

    @Test
    public void testExcludeDevelopDependencies() {
        PipfileLock pipfileLock = pipfileLock();
        PipfileLockDependencyVersionParser dependencyVersionParser = new PipfileLockDependencyVersionParser();
        PipfileLockTransformer parser = new PipfileLockTransformer(dependencyVersionParser, EnumListFilter.fromExcluded(PipenvDependencyType.DEV));
        List<PipfileLockDependency> dependencies = parser.transform(pipfileLock);

        Assertions.assertEquals(2, dependencies.size());
        Assertions.assertTrue(containsDependency(dependencies, "comp1", "1.0"));
        Assertions.assertTrue(containsDependency(dependencies, "comp2", null));
        Assertions.assertFalse(containsDependency(dependencies, "comp3", "3.0"));
    }

    private PipfileLock pipfileLock() {
        PipfileLock pipfileLock = new PipfileLock();

        Map<String, PipfileLockDependencyEntry> dependencies = new HashMap<>();
        dependencies.put("comp1", createEntry("==1.0"));
        dependencies.put("comp2", createEntry(null));
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
