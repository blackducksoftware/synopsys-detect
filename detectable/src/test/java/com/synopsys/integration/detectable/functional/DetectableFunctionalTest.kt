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
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

/*
A functional test creates a small sample detectable environment and verifies the detectable produces the expected Code Locations.
As with unit tests functional tests should be as small as possible.
Functional tests use real output from package managers. 
 */

abstract class DetectableFunctionalTest(val name: String) {

    val folder = Files.createTempDirectory(name).toFile()
    val source = File(folder, "source")
    val output = File(folder, "output")
    val fileFinder = FunctionalFileFinder()
    val executableRunner = FunctionalExecutableRunner()
    val externalIdFactory = ExternalIdFactory()
    val detectableFactory = DetectableFactory(fileFinder, executableRunner, externalIdFactory, GsonBuilder().create())

    fun addFileFromResource(name: String, resourcePath: String) {
        val inputStream = FunctionalTestFiles.asInputStream(resourcePath)
        Assertions.assertNotNull(inputStream, "Could not find resource file: $resourcePath")
        val destination = File(source, name)
        FileUtils.copyInputStreamToFile(inputStream, destination)
        fileFinder.addFile(destination, 0)
    }

    @Test
    fun run() {
        setup()

        val detectableEnvironment = DetectableEnvironment(source)
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
