package com.synopsys.integration.detector.finder;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.synopsys.integration.detector.base.DetectorEvaluationTree;
import com.synopsys.integration.detector.rule.DetectorRule;
import com.synopsys.integration.detector.rule.DetectorRuleSet;

public class DetectorFinderTest {
    private static Path initialDirectoryPath;

    @BeforeAll
    public static void setup() throws IOException {
        initialDirectoryPath = Files.createTempDirectory("DetectorFinderTest");
    }

    @AfterAll
    public static void cleanup() {
        initialDirectoryPath.toFile().delete();
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSimple() throws IOException, DetectorFinderDirectoryListException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final File initialDirectory = initialDirectoryPath.toFile();
        final File subDir = new File(initialDirectory, "testSimple");
        subDir.mkdirs();

        final File subSubDir1 = new File(subDir, "subSubDir1");
        subSubDir1.mkdir();

        final File subSubDir2 = new File(subDir, "subSubDir2");
        subSubDir2.mkdir();

        final DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<>(0), new HashMap<>(0));
        final Predicate<File> fileFilter = f -> { return true; };
        final int maximumDepth = 10;
        final DetectorFinderOptions options = new DetectorFinderOptions(fileFilter, maximumDepth);

        final DetectorFinder finder = new DetectorFinder();
        final Optional<DetectorEvaluationTree> tree = finder.findDetectors(initialDirectory, detectorRuleSet, options);

        // make sure the symlink was omitted from results
        final Set<DetectorEvaluationTree> subDirResults = tree.get().getChildren().iterator().next().getChildren();
        assertEquals(2, subDirResults.size());
        String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
        assertTrue(subDirContentsName.startsWith("subSubDir"));
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSymLinksNotFollowed() throws IOException, DetectorFinderDirectoryListException {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        // Create a subDir with a symlink that loops back to its parent
        final File initialDirectory = initialDirectoryPath.toFile();
        final File subDir = new File(initialDirectory, "testSymLinksNotFollowed");
        subDir.mkdirs();
        final File link = new File(subDir, "linkToInitial");
        final Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        final File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();

        final DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<>(0), new HashMap<>(0));
        final Predicate<File> fileFilter = f -> { return true; };
        final int maximumDepth = 10;
        final DetectorFinderOptions options = new DetectorFinderOptions(fileFilter, maximumDepth);

        final DetectorFinder finder = new DetectorFinder();
        final Optional<DetectorEvaluationTree> tree = finder.findDetectors(initialDirectory, detectorRuleSet, options);

        // make sure the symlink was omitted from results
        final Set<DetectorEvaluationTree> subDirResults = tree.get().getChildren().iterator().next().getChildren();
        assertEquals(1, subDirResults.size());
        String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
        assertEquals("regularDir", subDirContentsName);
    }

}
