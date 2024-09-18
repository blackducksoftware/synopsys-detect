package com.blackduck.integration.detectable.detectables.poetry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.Set;

import com.blackduck.integration.detectable.detectable.codelocation.CodeLocation;
import com.blackduck.integration.detectable.detectables.poetry.parser.PoetryLockParser;
import com.blackduck.integration.detectable.extraction.Extraction;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlTable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.util.NameVersion;

public class PoetryExtractor {
    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";

    private final PoetryLockParser poetryLockParser;

    public PoetryExtractor(PoetryLockParser poetryLockParser) {
        this.poetryLockParser = poetryLockParser;
    }

    public Extraction extract(File poetryLock, @Nullable TomlTable toolDotPoetrySection, Set<String> rootPackages) {
        try {
            DependencyGraph graph = poetryLockParser.parseLockFile(
                FileUtils.readFileToString(poetryLock, StandardCharsets.UTF_8),
                rootPackages
            );
            CodeLocation codeLocation = new CodeLocation(graph);

            Optional<NameVersion> poetryNameVersion = extractNameVersionFromToolDotPoetrySection(toolDotPoetrySection);
            if (poetryNameVersion.isPresent()) {
                return new Extraction.Builder()
                    .success(codeLocation)
                    .projectName(poetryNameVersion.get().getName())
                    .projectVersion(poetryNameVersion.get().getVersion())
                    .build();
            }
            return new Extraction.Builder().success(codeLocation).build();
        } catch (IOException e) {
            return new Extraction.Builder().exception(e).build();
        }
    }

    private Optional<NameVersion> extractNameVersionFromToolDotPoetrySection(@Nullable TomlTable toolDotPoetry) {
        if (toolDotPoetry != null) {
            if (toolDotPoetry.get(NAME_KEY) != null && toolDotPoetry.get(VERSION_KEY) != null) {
                return Optional.of(new NameVersion(toolDotPoetry.getString(NAME_KEY), toolDotPoetry.getString(VERSION_KEY)));
            }
        }
        return Optional.empty();
    }

}
