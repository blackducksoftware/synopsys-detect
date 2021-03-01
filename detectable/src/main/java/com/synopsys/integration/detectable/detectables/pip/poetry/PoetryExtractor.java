/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip.poetry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pip.poetry.parser.PoetryLockParser;
import com.synopsys.integration.util.NameVersion;

public class PoetryExtractor {

    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";
    private static final String TOOL_KEY = "tool.poetry";

    private final PoetryLockParser poetryLockParser;

    public PoetryExtractor(final PoetryLockParser poetryLockParser) {
        this.poetryLockParser = poetryLockParser;
    }

    public Extraction extract(File poetryLock, Optional<File> pyprojectToml) {
        try {
            final DependencyGraph graph = poetryLockParser.parseLockFile(FileUtils.readFileToString(poetryLock, StandardCharsets.UTF_8));
            final CodeLocation codeLocation = new CodeLocation(graph);

            Optional<NameVersion> poetryNameVersion = extractNameVersionFromPyProjectToml(pyprojectToml);
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

    private Optional<NameVersion> extractNameVersionFromPyProjectToml(Optional<File> pyprojectToml) throws IOException {
        if (pyprojectToml.isPresent()) {
            TomlParseResult cargoTomlObject = Toml.parse(getFileAsString(pyprojectToml.get(), Charset.defaultCharset()));
            if (cargoTomlObject.get(TOOL_KEY) != null) {
                TomlTable cargoTomlPackageInfo = cargoTomlObject.getTable(TOOL_KEY);
                if (cargoTomlPackageInfo.get(NAME_KEY) != null && cargoTomlPackageInfo.get(VERSION_KEY) != null) {
                    return Optional.of(new NameVersion(cargoTomlPackageInfo.getString(NAME_KEY), cargoTomlPackageInfo.getString(VERSION_KEY)));
                }
            }
        }
        return Optional.empty();
    }
}
