package com.blackduck.integration.detectable.detectables.conan.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.blackduck.integration.detectable.annotations.FunctionalTest;
import com.blackduck.integration.detectable.detectable.util.EnumListFilter;
import com.blackduck.integration.detectable.detectables.conan.ConanCodeLocationGenerator;
import com.blackduck.integration.detectable.detectables.conan.ConanDetectableResult;
import com.blackduck.integration.detectable.detectables.conan.cli.config.ConanDependencyType;
import com.blackduck.integration.detectable.detectables.conan.lockfile.parser.ConanLockfileParser;
import com.blackduck.integration.detectable.util.FunctionalTestFiles;
import com.blackduck.integration.detectable.util.GraphCompare;
import com.synopsys.integration.exception.IntegrationException;

@FunctionalTest
public class ConanLockfileParserFunctionalTest {

    @Test
    public void testNoProjectRef() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, false), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        assertEquals(3, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();
        GraphCompare.assertEqualsResource("/conan/lockfile/noProjectRef_graph.json", actualDependencyGraph);
    }

    @Test
    public void testWithDevDependencies() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan_buildrequirements.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, false), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        assertEquals(3, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();
        GraphCompare.assertEqualsResource("/conan/lockfile/noProjectRef_graph.json", actualDependencyGraph);
    }

    @Test
    public void testNoProjectRefLongFormExternalIds() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, true), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        assertEquals(3, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();
        GraphCompare.assertEqualsResource("/conan/lockfile/noProjectRefLongForm_graph.json", actualDependencyGraph);
    }

    @Test
    public void testProjectRef() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan_projectref.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, true), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        assertEquals("chat", result.getProjectName());
        assertEquals("0.1", result.getProjectVersion());
    }

    @Test
    public void testRelativePath() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan_relpath.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, true), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        assertEquals(1, result.getCodeLocation().getDependencyGraph().getRootDependencies().size());
    }

    @Test
    public void testConan2Lockfile() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan2/conan.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.excludeNone();
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, true), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();
        assertEquals(8, actualDependencyGraph.getRootDependencies().size());
        GraphCompare.assertEqualsResource("/conan/lockfile/conan2/includeBuild_graph.json", actualDependencyGraph);
    }

    @Test
    public void testConan2LockfileExcludeBuild() throws IOException, IntegrationException {
        File lockfile = FunctionalTestFiles.asFile("/conan/lockfile/conan2/conan.lock");
        EnumListFilter<ConanDependencyType> dependencyTypeFilter = EnumListFilter.fromExcluded(ConanDependencyType.BUILD);
        ConanLockfileParser parser = new ConanLockfileParser(new Gson(), new ConanCodeLocationGenerator(dependencyTypeFilter, true), new ExternalIdFactory());
        String conanLockfileContents = FileUtils.readFileToString(lockfile, StandardCharsets.UTF_8);

        ConanDetectableResult result = parser.generateCodeLocationFromConanLockfileContents(conanLockfileContents);

        DependencyGraph actualDependencyGraph = result.getCodeLocation().getDependencyGraph();
        assertEquals(7, actualDependencyGraph.getRootDependencies().size());
        GraphCompare.assertEqualsResource("/conan/lockfile/conan2/excludeBuild_graph.json", actualDependencyGraph);
    }
}
