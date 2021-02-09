/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.detectable.detectable.util.TomlFileParser;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.detectable.codelocation.CodeLocation;
import com.synopsys.integration.detectable.detectables.pip.poetry.parser.PoetryLockParser;
import com.synopsys.integration.util.NameVersion;

public class PoetryExtractor {
    private static final String NAME_KEY = "name";
    private static final String VERSION_KEY = "version";

    private final PoetryLockParser poetryLockParser;

    public PoetryExtractor(final PoetryLockParser poetryLockParser) {
        this.poetryLockParser = poetryLockParser;
    }

    public Extraction extract(File poetryLock, TomlTable toolDotPoetrySection) {
        try {
            final DependencyGraph graph = poetryLockParser.parseLockFile(FileUtils.readFileToString(poetryLock, StandardCharsets.UTF_8));
            final CodeLocation codeLocation = new CodeLocation(graph);

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

    private Optional<NameVersion> extractNameVersionFromToolDotPoetrySection(TomlTable toolDotPoetry) {
        if (toolDotPoetry.get(NAME_KEY) != null && toolDotPoetry.get(VERSION_KEY) != null) {
            return Optional.of(new NameVersion(toolDotPoetry.getString(NAME_KEY), toolDotPoetry.getString(VERSION_KEY)));
        }
        return Optional.empty();
    }

}
