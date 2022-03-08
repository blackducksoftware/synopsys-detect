package com.synopsys.integration.detectable.detectables.poetry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlTable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.poetry.parser.PoetryLockParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.util.NameVersion;

public class PoetryExtractor {
    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";

    private final PoetryLockParser poetryLockParser;

    public PoetryExtractor(PoetryLockParser poetryLockParser) {
        this.poetryLockParser = poetryLockParser;
    }

    public Extraction extract(File poetryLock, @Nullable TomlTable toolDotPoetrySection) {
        try {
            DependencyGraph graph = poetryLockParser.parseLockFile(FileUtils.readFileToString(poetryLock, StandardCharsets.UTF_8));
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

    private String getFileAsString(File cargoLock, Charset encoding) throws IOException {
        List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
        return String.join(System.lineSeparator(), goLockAsList);
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
