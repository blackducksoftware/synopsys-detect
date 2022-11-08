package com.synopsys.integration.detectable.detectables.git.functional;

import static com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor.EXTRACTION_METADATA_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;

public class GitParseDetectableTest extends DetectableFunctionalTest {
    public GitParseDetectableTest() throws IOException {
        super("git-parse");
    }

    @Override
    public void setup() throws IOException {
        Path gitDirectory = addDirectory(Paths.get(".git"));
        addFile(
            gitDirectory.resolve("config"),
            "[core]",
            "	repositoryformatversion = 0",
            "	filemode = true",
            "	bare = false",
            "	logallrefupdates = true",
            "	ignorecase = true",
            "	precomposeunicode = true",
            "[remote \"origin\"]",
            "	url = https://github.com/blackducksoftware/synopsys-detect.git",
            "	fetch = +refs/heads/*:refs/remotes/origin/*",
            "[branch \"master\"]",
            "	remote = origin",
            "	merge = refs/heads/master",
            "[branch \"test\"]",
            "	remote = origin",
            "	merge = refs/heads/test"
        );

        addFile(gitDirectory.resolve("HEAD"), Collections.singletonList("ref: refs/heads/master\n"));

        addFile(gitDirectory.resolve("ORIG_HEAD"), "c173aa3ea902e8ccef6d78137f3a48e275a1a820");
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createGitParseDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
        assertEquals("blackducksoftware/synopsys-detect", extraction.getProjectName());
        assertEquals("master", extraction.getProjectVersion());

        assertTrue(extraction.hasMetadata(EXTRACTION_METADATA_KEY), "Extraction should include GitInfo as MetaData");
        Optional<GitInfo> metaData = extraction.getMetaData(EXTRACTION_METADATA_KEY);
        assertTrue(metaData.isPresent(), "Extraction reported having the GitInfo key, but the value isn't set.");

        assertTrue(metaData.get().getSourceRepository().isPresent(), "Expected a source url to be parsed.");
        assertEquals("https://github.com/blackducksoftware/synopsys-detect.git", metaData.get().getSourceRepository().get());

        assertTrue(metaData.get().getSourceBranch().isPresent(), "Expected a branch to be parsed.");
        assertEquals("master", metaData.get().getSourceBranch().get());

        assertTrue(metaData.get().getSourceRevision().isPresent(), "Expected a commit hash to be parsed.");
        assertEquals("c173aa3ea902e8ccef6d78137f3a48e275a1a820", metaData.get().getSourceRevision().get());
    }
}