package com.synopsys.integration.detector.finder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.SystemUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.synopsys.integration.common.util.finder.SimpleFileFinder;

public class DirectoryFinderTest {
    private static Path initialDirectoryPath;

    @BeforeAll
    public static void setup() throws IOException {
        initialDirectoryPath = Files.createTempDirectory("DetectorFinderTest");
    }

    @AfterAll
    public static void cleanup() throws IOException {
        if (Files.exists(initialDirectoryPath)) {
            try (Stream<Path> walk = Files.walk(initialDirectoryPath)) {
                walk.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
        }
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSimple() {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        File initialDirectory = initialDirectoryPath.toFile();
        File subDir = new File(initialDirectory, "testSimple");
        subDir.mkdirs();

        File subSubDir1 = new File(subDir, "subSubDir1");
        subSubDir1.mkdir();

        File subSubDir2 = new File(subDir, "subSubDir2");
        subSubDir2.mkdir();

        Predicate<File> fileFilter = f -> true;
        int maximumDepth = 10;
        DirectoryFinderOptions options = new DirectoryFinderOptions(fileFilter, maximumDepth, false);

        DirectoryFinder finder = new DirectoryFinder();
        Optional<DirectoryFindResult> tree = finder.findDirectories(initialDirectory, options, new SimpleFileFinder());

        // make sure both dirs were found
        Set<DirectoryFindResult> testDirs = tree.get().getChildren();
        DirectoryFindResult simpleTestDir = null;
        for (DirectoryFindResult testDir : testDirs) {
            if (testDir.getDirectory().getName().equals("testSimple")) {
                simpleTestDir = testDir;
                break;
            }
        }
        Set<DirectoryFindResult> subDirResults = simpleTestDir.getChildren();
        assertEquals(2, subDirResults.size());
        String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
        assertTrue(subDirContentsName.startsWith("subSubDir"));
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSymLinksNotFollowed() throws IOException {
        testSymLinks(false);
    }

    @Test
    @DisabledOnOs(WINDOWS) //TODO: See if we can fix on windows.
    public void testSymLinksFollowed() throws IOException {
        testSymLinks(true);
    }

    private void testSymLinks(boolean followSymLinks) throws IOException {
        Assumptions.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        File initialDirectory = createDirWithSymLink("testSymLinks");

        DirectoryFinderOptions options = createFinderOptions(followSymLinks);

        DirectoryFinder finder = new DirectoryFinder();
        Optional<DirectoryFindResult> tree = finder.findDirectories(initialDirectory, options, new SimpleFileFinder());

        // make sure the symlink was omitted from results
        //        final Set<DirectoryFindResult> subDirResults = tree.get().getChildren().iterator().next().getChildren();
        Set<DirectoryFindResult> testDirs = tree.get().getChildren();
        DirectoryFindResult symLinkTestDir = null;
        for (DirectoryFindResult testDir : testDirs) {
            if (testDir.getDirectory().getName().equals("testSymLinks")) {
                symLinkTestDir = testDir;
                break;
            }
        }
        Set<DirectoryFindResult> subDirResults = symLinkTestDir.getChildren();

        if (followSymLinks) {
            assertEquals(2, subDirResults.size());
        } else {
            assertEquals(1, subDirResults.size());
            String subDirContentsName = subDirResults.iterator().next().getDirectory().getName();
            assertEquals("regularDir", subDirContentsName);
        }

        try (Stream<Path> walk = Files.walk(initialDirectory.toPath())) {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        }
    }
    
    @NotNull
    private DirectoryFinderOptions createFinderOptions(boolean followSymLinks) {
        Predicate<File> fileFilter = f -> true;
        int maximumDepth = 10;
        return new DirectoryFinderOptions(fileFilter, maximumDepth, followSymLinks);
    }

    @NotNull
    private File createDirWithSymLink(String dirName) throws IOException {
        // Create a subDir with a symlink that loops back to its parent
        File initialDirectory = initialDirectoryPath.toFile();
        File subDir = new File(initialDirectory, dirName);
        subDir.mkdirs();
        File link = new File(subDir, "linkToInitial");
        Path linkPath = link.toPath();
        Files.createSymbolicLink(linkPath, initialDirectoryPath);

        File regularDir = new File(subDir, "regularDir");
        regularDir.mkdir();
        return initialDirectory;
    }

}
