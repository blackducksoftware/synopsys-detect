/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.functional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.Extraction;
import com.synopsys.integration.detectable.ExtractionEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.Executable;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.file.FileFinder;
import com.synopsys.integration.detectable.detectable.file.impl.SimpleFileFinder;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.factory.DetectableFactory;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;

public abstract class DetectableFunctionalTest {

    @NotNull
    private final String name;

    @NotNull
    private final Path tempDirectory;

    @NotNull
    private final Path sourceDirectory;

    @NotNull
    private final Path outputDirectory;

    @NotNull
    private final FunctionalExecutableRunner executableRunner;

    @NotNull
    public final DetectableFactory detectableFactory;

    protected DetectableFunctionalTest(@NotNull final String name) throws IOException {
        this.name = name;

        this.tempDirectory = Files.createTempDirectory(name);
        this.sourceDirectory = tempDirectory.resolve("source");
        this.outputDirectory = tempDirectory.resolve("output");

        this.executableRunner = new FunctionalExecutableRunner();

        final FileFinder fileFinder = new SimpleFileFinder();
        final ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        final Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.detectableFactory = new DetectableFactory(fileFinder, executableRunner, externalIdFactory, gson);
    }

    @Test
    public void run() throws IOException, DetectableException {
        System.out.println(String.format("Function Test (%s) is using temp directory: %s", name, tempDirectory.toString()));

        setup();

        final DetectableEnvironment detectableEnvironment = new DetectableEnvironment(sourceDirectory.toFile());
        final Detectable detectable = create(detectableEnvironment);
        final DetectableResult applicable = detectable.applicable();
        Assertions.assertTrue(applicable.getPassed(), String.format("Applicable should have passed but was: %s", applicable.toDescription()));

        final DetectableResult extractable = detectable.extractable();
        Assertions.assertTrue(extractable.getPassed(), String.format("Extractable should have passed but was: %s", extractable.toDescription()));

        final ExtractionEnvironment extractionEnvironment = new ExtractionEnvironment(outputDirectory.toFile());
        final Extraction extraction = detectable.extract(extractionEnvironment);

        assertExtraction(extraction);

        FileUtils.deleteDirectory(tempDirectory.toFile());
    }

    @NotNull
    public Path addFile(@NotNull final Path path) throws IOException {
        return addFile(path, Collections.emptyList());
    }

    @NotNull
    public Path addFile(@NotNull final Path path, @NotNull final String... lines) throws IOException {
        final List<String> fileContent = Arrays.asList(lines);
        return addFile(path, fileContent);
    }

    @NotNull
    public Path addFile(@NotNull final Path path, @NotNull final Iterable<? extends String> lines) throws IOException {
        final Path relativePath = sourceDirectory.resolve(path);
        Files.createDirectories(relativePath.getParent());
        return Files.write(relativePath, lines);
    }

    @NotNull
    public Path addFileFromResources(@NotNull final Path path, @NotNull final String resourcePath) throws IOException {
        final List<String> lines = FunctionalTestFiles.asListOfStrings(resourcePath);
        return addFile(path, lines);
    }

    @NotNull
    public Path addDirectory(@NotNull final Path path) throws IOException {
        final Path relativePath = sourceDirectory.resolve(path);
        return Files.createDirectories(relativePath);
    }

    public void addExecutableOutput(@NotNull final ExecutableOutput executableOutput, @NotNull final String... command) {
        addExecutableOutput(executableOutput, new HashMap<>(), command);
    }

    public void addExecutableOutput(@NotNull final ExecutableOutput executableOutput, @NotNull final Map<String, String> environment, @NotNull final String... command) {
        final List<String> commandList = Arrays.asList(command);
        final Executable executable = new Executable(sourceDirectory.toFile(), environment, commandList);
        executableRunner.addExecutableOutput(executable, executableOutput);
    }

    protected abstract void setup() throws IOException;

    @NotNull
    public abstract Detectable create(@NotNull final DetectableEnvironment detectableEnvironment);

    public abstract void assertExtraction(@NotNull final Extraction extraction);
}
