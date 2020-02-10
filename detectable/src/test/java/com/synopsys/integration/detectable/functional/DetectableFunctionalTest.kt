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
package com.synopsys.integration.detectable.functional

import com.google.gson.GsonBuilder
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory
import com.synopsys.integration.detectable.Detectable
import com.synopsys.integration.detectable.DetectableEnvironment
import com.synopsys.integration.detectable.Extraction
import com.synopsys.integration.detectable.ExtractionEnvironment
import com.synopsys.integration.detectable.factory.DetectableFactory
import com.synopsys.integration.detectable.util.FunctionalTestFiles
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/*
A functional test creates a small sample detectable environment and verifies the detectable produces the expected Code Locations.
As with unit tests functional tests should be as small as possible.
Functional tests use real output from package managers. 
 */

abstract class DetectableFunctionalTest(val name: String) {

    val folder = Files.createTempDirectory(name).toFile()
    val source = TestDirectory(folder.toPath().resolve("source")) // TestFile(folder, "source")
    val output = File(folder, "output")
    val fileFinder = FunctionalFileFinder()
    val executableRunner = FunctionalExecutableRunner()
    val externalIdFactory = ExternalIdFactory()
    val detectableFactory = DetectableFactory(fileFinder, executableRunner, externalIdFactory, GsonBuilder().create())

    fun addFiles(init: TestDirectory.() -> Unit): TestDirectory {
        source.init()

        addToFileFinder(source)
        return source
    }

    private fun addToFileFinder(directory: TestDirectory, depth: Int = 0) {
        fileFinder.addFile(directory.path.toFile(), depth)
        directory.listFiles.forEach {
            fileFinder.addFile(it.path.toFile(), depth)
            if (it is TestDirectory) {
                addToFileFinder(it, depth + 1)
            }
        }
    }

    @Test
    fun run() {
        setup()

        val detectableEnvironment = DetectableEnvironment(source.path.toFile())
        val detectable = create(detectableEnvironment)
        val applicable = detectable.applicable()
        Assertions.assertTrue(applicable.passed, "Applicable should have passed but was: ${applicable.toDescription()}")

        val extractable = detectable.extractable()
        Assertions.assertTrue(extractable.passed, "Extractable should have passed but was: ${extractable.toDescription()}")

        val extractionEnvironment = ExtractionEnvironment(output)
        val extraction = detectable.extract(extractionEnvironment)

        Assertions.assertTrue(extraction.codeLocations.size > 0)

        assert(extraction)
    }

    abstract fun setup()
    abstract fun create(environment: DetectableEnvironment): Detectable
    abstract fun assert(extraction: Extraction)
}

sealed class FileSystemElement(val path: Path)
class TestDirectory(path: Path) : FileSystemElement(path) {
    val listFiles = mutableListOf<FileSystemElement>()

    init {
        if (!Files.exists(path)) {
            Files.createDirectory(path)
        }
    }

    fun file(name: String, fileContent: String): TestFile {
        val newPath = path.resolve(name)
        val file = TestFile(newPath, fileContent)
        listFiles.add(file)
        return file
    }

    fun fileFromResources(name: String, resourcePath: String, encoding: Charset = StandardCharsets.UTF_8): TestFile {
        val inputStream = FunctionalTestFiles.asInputStream(resourcePath)
        Assertions.assertNotNull(inputStream, "Could not find resource file: $resourcePath")
        return file(name, IOUtils.toString(inputStream, encoding))
    }

    fun directory(name: String): TestDirectory {
        val newPath = Paths.get(name)
        val directory = TestDirectory(newPath)
        listFiles.add(directory)
        return directory
    }

    fun directory(name: String, init: TestDirectory.() -> Unit): TestDirectory {
        val newPath = Paths.get(name)
        val directory = TestDirectory(newPath)
        directory.init()
        listFiles.add(directory)
        return directory
    }
}

class TestFile(path: Path, val content: String) : FileSystemElement(path) {
    init {
        Files.write(path, content.split(System.lineSeparator()))
    }
}