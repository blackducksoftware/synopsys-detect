package com.synopsys.integration.detectable.detectables.git.functional;

import static com.synopsys.integration.detectable.detectables.git.cli.GitCliExtractor.EXTRACTION_METADATA_KEY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.blackduck.bdio2.model.GitInfo;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.functional.DetectableFunctionalTest;

public class GitParseSafeDetectableTest extends DetectableFunctionalTest {
    public GitParseSafeDetectableTest() throws IOException {
        super("git-parse-safe");
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
            "	precomposeunicode = true"
            // The output GitParse needs is missing
        );

        // Empty HEAD file
        addFile(gitDirectory.resolve("HEAD"), "");

        // Missing ORIG_HEAD file
    }

    @NotNull
    @Override
    public Detectable create(@NotNull DetectableEnvironment environment) {
        return detectableFactory.createGitParseDetectable(environment);
    }

    @Override
    public void assertExtraction(@NotNull Extraction extraction) {
        // We should get no data, but no failures either
        assertEquals(0, extraction.getCodeLocations().size(), "Git should not produce a dependency graph. It is for project info only.");
        assertNull(extraction.getProjectName());
        assertNull(extraction.getProjectVersion());

        assertTrue(extraction.hasMetadata(EXTRACTION_METADATA_KEY), "Extraction should include GitInfo as MetaData");
        Optional<GitInfo> metaData = extraction.getMetaData(EXTRACTION_METADATA_KEY);
        assertTrue(metaData.isPresent(), "Extraction reported having the GitInfo key, but the value isn't set.");

        assertFalse(metaData.get().getSourceRepository().isPresent(), "Expected a source url to be missing.");
        assertFalse(metaData.get().getSourceBranch().isPresent(), "Expected a branch to be missing.");
        assertFalse(metaData.get().getSourceRevision().isPresent(), "Expected a commit hash to be missing.");
    }
}