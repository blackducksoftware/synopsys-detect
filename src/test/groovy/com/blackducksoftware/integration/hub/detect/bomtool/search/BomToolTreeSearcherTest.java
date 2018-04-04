package com.blackducksoftware.integration.hub.detect.bomtool.search;

import java.io.File;
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
import com.blackducksoftware.integration.hub.detect.bomtool.NestedBomTool;
import com.blackducksoftware.integration.test.TestLogger;

@ContextConfiguration(classes = { Application.class })
@RunWith(SpringJUnit4ClassRunner.class)
public class BomToolTreeSearcherTest {
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private File sourceDirectoryWithMultipleYarn;

    private File sourceDirectoryWithNestedNPM;

    private File sourceDirectoryWithNestedNPMInsideNodeModules;

    @Autowired
    private Set<NestedBomTool> nestedBomTools;

    @Before
    public void setupSearchStructure() throws Exception {
        sourceDirectoryWithMultipleYarn = folder.newFolder();
        File baseYarnLock = new File(sourceDirectoryWithMultipleYarn, "yarn.lock");
        File yarnDirectory = new File(sourceDirectoryWithMultipleYarn, "yarnDir");
        yarnDirectory.mkdirs();
        File subYarnLock = new File(yarnDirectory, "yarn.lock");

        sourceDirectoryWithNestedNPM = folder.newFolder();
        File npmDirectory = new File(sourceDirectoryWithNestedNPM, "npmDir");
        File subNpmDirectory = new File(npmDirectory, "subNpmDirectory");
        subNpmDirectory.mkdirs();
        File npmPackageLock = new File(subNpmDirectory, "package-lock.json");

        sourceDirectoryWithNestedNPMInsideNodeModules = folder.newFolder();
        File nodeModulesDirectory = new File(sourceDirectoryWithNestedNPMInsideNodeModules, "node_modules");
        nodeModulesDirectory.mkdirs();
        File nodeModulesNpmPackageLock = new File(nodeModulesDirectory, "package-lock.json");
    }

    @Test
    public void testSearchBomToolSearchYarn() throws Exception {
        final int maximumDepth = 0;

        BomToolTreeSearcher bomToolTreeSearcher = new BomToolTreeSearcher(new TestLogger(), false);

        bomToolTreeSearcher.startSearching(null, nestedBomTools, sourceDirectoryWithMultipleYarn, 0);
    }

}
