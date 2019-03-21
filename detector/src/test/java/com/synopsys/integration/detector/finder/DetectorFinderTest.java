package com.synopsys.integration.detector.finder;

import static com.sun.javafx.PlatformUtil.isWindows;
import static org.junit.Assert.assertEquals;

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
    public void testSymLinksNotFollowed() throws IOException, DetectorFinderDirectoryListException {
        org.junit.Assume.assumeFalse(isWindows());

        // Create a subDir with a symlink that loops back to its parent
        final File initialDirectory = initialDirectoryPath.toFile();
        final File subDir = new File(initialDirectory, "sub");
        subDir.mkdirs();
        final File link = new File(subDir, "linkToInitial");
        final Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        final File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();

        final DetectorRuleSet detectorRuleSet = new DetectorRuleSet(new ArrayList<DetectorRule>(0), new HashMap<DetectorRule, Set<DetectorRule>>(0));
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
