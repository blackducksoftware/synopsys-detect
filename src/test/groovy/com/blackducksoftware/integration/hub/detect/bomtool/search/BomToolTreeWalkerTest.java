package com.blackducksoftware.integration.hub.detect.bomtool.search;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.hub.detect.Application;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolApplicableResult;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolFinder;
import com.blackducksoftware.integration.util.ResourceUtil;

@ContextConfiguration(classes = { Application.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class BomToolTreeWalkerTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File sourceDirectoryWithMultipleYarn;

    private File sourceDirectoryWithNestedNPM;

    private File sourceDirectoryWithNestedNPMInsideNodeModules;

    @Autowired
    private Set<BomTool> nestedBomTools;

    @Before
    public void setupSearchStructure() throws Exception {
        sourceDirectoryWithMultipleYarn = folder.newFolder();
        final File yarnBaseDir = new File(sourceDirectoryWithMultipleYarn, "yarnBaseDir");
        yarnBaseDir.mkdirs();
        final String yarnLockContent = ResourceUtil.getResourceAsString(BomToolTreeWalkerTest.class, "/yarn/yarn.lock", StandardCharsets.UTF_8);
        final File baseYarnLock = new File(yarnBaseDir, "yarn.lock");
        Files.write(baseYarnLock.toPath(), yarnLockContent.getBytes(StandardCharsets.UTF_8));
        final File yarnDirectory = new File(yarnBaseDir, "yarnDir");
        yarnDirectory.mkdirs();
        final File subYarnLock = new File(yarnDirectory, "yarn.lock");
        Files.write(subYarnLock.toPath(), yarnLockContent.getBytes(StandardCharsets.UTF_8));

        final String npmPackageLockContent = ResourceUtil.getResourceAsString(BomToolTreeWalkerTest.class, "/npm/package-lock.json", StandardCharsets.UTF_8);
        sourceDirectoryWithNestedNPM = folder.newFolder();
        final File npmBaseDir = new File(sourceDirectoryWithNestedNPM, "npmBaseDir");
        final File npmDirectory = new File(npmBaseDir, "npmDir");
        final File subNpmDirectory = new File(npmDirectory, "subNpmDirectory");
        subNpmDirectory.mkdirs();
        final File npmPackageLock = new File(subNpmDirectory, "package-lock.json");
        Files.write(npmPackageLock.toPath(), npmPackageLockContent.getBytes(StandardCharsets.UTF_8));

        sourceDirectoryWithNestedNPMInsideNodeModules = folder.newFolder();
        final File npmBaseWithNodeModulesDir = new File(sourceDirectoryWithNestedNPMInsideNodeModules, "npmBaseDir");
        npmBaseWithNodeModulesDir.mkdirs();
        final File nodeModulesDirectory = new File(npmBaseWithNodeModulesDir, "node_modules");
        nodeModulesDirectory.mkdirs();
        final File nodeModulesNpmPackageLock = new File(nodeModulesDirectory, "package-lock.json");
        Files.write(nodeModulesNpmPackageLock.toPath(), npmPackageLockContent.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void testSearchBomToolSearchYarnNoDepth() throws Exception {
        final int maximumDepth = 0;

        final BomToolFinder bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, false, maximumDepth);

        final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn);

        assertEquals(0, results.size());
    }

    @Test
    public void testSearchBomToolSearchYarnDepth1() throws Exception {
        final int maximumDepth = 1;

        final BomToolFinder bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, false, maximumDepth);

        final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn);

        assertEquals(1, results.size());
    }

    @Test
    public void testSearchBomToolSearchYarnDepth2() throws Exception {
        final int maximumDepth = 2;

        final BomToolFinder bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, false, maximumDepth);

        final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn);
        // Should have only found one because the yarn projects are nested
        assertEquals(1, results.size());
    }

    @Test
    public void testSearchBomToolSearchYarnDepth2Forced() throws Exception {
        final int maximumDepth = 2;

        final BomToolFinder bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, true, maximumDepth);

        final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn);

        assertEquals(2, results.size());
    }

    @Test
    public void testSearchBomToolSearchNpm() throws Exception {
        int maximumDepth = 2;

        BomToolFinder bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, false, maximumDepth);

        List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPM);
        // Should not have found anything because the Npm project is deeper than 2 directories down
        assertEquals(0, results.size());

        maximumDepth = 3;
        bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, false, maximumDepth);
        results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPM);
        assertEquals(1, results.size());
    }

    @Test
    public void testSearchBomToolSearchNpmWithinNodeModules() throws Exception {
        final int maximumDepth = 2;

        BomToolFinder bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), true, false, maximumDepth);

        List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPMInsideNodeModules);
        // Should not have found the Npm project because it is in a node_modules directory
        assertEquals(0, results.size());

        bomToolTreeWalker = new BomToolFinder(Collections.emptyList(), false, true, maximumDepth);

        results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPMInsideNodeModules);
        // Should  have found the Npm project because we are continuing and we are not excluding any directories
        assertEquals(1, results.size());

        bomToolTreeWalker = new BomToolFinder(Arrays.asList("node_modules"), false, true, maximumDepth);

        results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPMInsideNodeModules);
        // Should  have found the Npm project because we are continuing and we are not excluding any directories
        assertEquals(0, results.size());
    }

}
