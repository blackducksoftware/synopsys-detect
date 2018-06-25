package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.blackducksoftware.integration.hub.detect.Application;
import com.blackducksoftware.integration.hub.detect.bomtool.BomTool;
import com.blackducksoftware.integration.hub.detect.search.BomToolFinder;
import com.blackducksoftware.integration.util.ResourceUtil;

@Ignore
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

    private List<String> getDefaults() {
        return getDefaults(null);
    }

    private List<String> getDefaults(List<String> existing) {
        if (existing == null) {
            existing = new ArrayList<>();
        }
        try {
            final String fileContent = ResourceUtil.getResourceAsString(BomToolFinder.class, "/excludedDirectoriesBomToolSearch.txt", StandardCharsets.UTF_8);
            existing.addAll(Arrays.asList(fileContent.split("\n")));
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return existing;
    }

    @Test
    public void testSearchBomToolSearchYarnNoDepth() throws Exception {
        // TODO: Re-implement this test
        // final BomToolFinderOptions options = new BomToolFinderOptions(getDefaults(), false, 0); final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        //
        // final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn, options);
        //
        // assertEquals(0, results.size());
    }

    @Test
    public void testSearchBomToolSearchYarnDepth1() throws Exception {
        // TODO: Re-implement this test
        // final BomToolFinderOptions options = new BomToolFinderOptions(getDefaults(), false, 1);
        // final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        //
        // final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn, options);
        //
        // assertEquals(1, results.size());
    }

    @Test
    public void testSearchBomToolSearchYarnDepth2() throws Exception {
        // TODO: Re-implement this test
        // final BomToolFinderOptions options = new BomToolFinderOptions(getDefaults(), false, 2); final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        //
        // final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn, options); // Should have only found one because the yarn projects are nested assertEquals(1,
        // results.size());
    }

    @Test
    public void testSearchBomToolSearchYarnDepth2Forced() throws Exception {
        // TODO: Re-implement this test
        // final BomToolFinderOptions options = new BomToolFinderOptions(getDefaults(), false, 2);
        // final BomToolFinder bomToolTreeWalker = new BomToolFinder();
        //
        // final List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithMultipleYarn, options);
        //
        // assertEquals(2, results.size());
    }

    @Test
    public void testSearchBomToolSearchNpm() throws Exception {
        // TODO: Re-implement this test
        // final BomToolFinderOptions options = new BomToolFinderOptions(getDefaults(), false, 2);
        // BomToolFinder bomToolTreeWalker = new BomToolFinder();
        //
        // List<BomToolApplicableResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPM, options);
        // // Should not have found anything because the Npm project is deeper than 2 directories down
        // assertEquals(0, results.size());
        //
        // final BomToolFinderOptions options3 = new BomToolFinderOptions(getDefaults(), false, 3);
        // bomToolTreeWalker = new BomToolFinder();
        // results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPM, options3);
        // assertEquals(1, results.size());
    }

    @Test
    public void testSearchBomToolSearchNpmWithinNodeModules() throws Exception {
        // TODO: Re-implement this test
        // BomToolFinder bomToolTreeWalker = new BomToolFinder(); final BomToolFinderOptions options = new BomToolFinderOptions(getDefaults(), false, 2);
        //
        // List<BomToolResult> results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPMInsideNodeModules, options);
        // // Should not have found the Npm project because it is in a node_modules directory
        // assertEquals(0, results.size());
        //
        // bomToolTreeWalker = new BomToolFinder();
        //
        // results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPMInsideNodeModules, options);
        // // Should have found the Npm project because we are continuing and we are not excluding any directories
        // assertEquals(1, results.size());
        //
        // final BomToolFinderOptions optionsExcl = new BomToolFinderOptions(Arrays.asList("node_modules"), false, 2); bomToolTreeWalker = new BomToolFinder();
        //
        // results = bomToolTreeWalker.findApplicableBomTools(nestedBomTools, sourceDirectoryWithNestedNPMInsideNodeModules, optionsExcl);
        // // Should have found the Npm project because we are continuing and we are not excluding any
        // directories assertEquals(0, results.size());
    }

}
