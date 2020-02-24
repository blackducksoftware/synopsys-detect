/**
 * synopsys-detect
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
package com.synopsys.integration.detect.configuration

import com.synopsys.integration.configuration.property.Property
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets

@Disabled // For now these tests are not actually helping anyone. Will be tagged with "lint" in the future.
class DeprecatedPropertyReferenceTest {
    private val fileTypesToCheck = listOf(
            "java",
            "kt",
            "groovy",
            "kts"
    )
    private val excludedFileNames = listOf(
            DetectConfigurationFactory::class.java,
            DetectableOptionFactory::class.java,
            DetectProperties::class.java,
            this.javaClass
    ).map { it.simpleName }

    private val deprecatedPropertyReferenceStrings: List<String> = DetectProperties.properties
            .filter { it.propertyDeprecationInfo != null }
            .map(Property::getKey)
            .map(String::toUpperCase)
            .map { key -> key.replace(".", "_") }

    @Test
    @Throws(IOException::class)
    fun testCodeReferencesToDeprecatedProperties() {
        val rootDir = File("src")
        val sourceFiles = FileUtils.listFiles(rootDir, fileTypesToCheck.toTypedArray(), true)
        val notAllowedFiles = sourceFiles.filter { file -> !excludedFileNames.contains(file.nameWithoutExtension) }
        val issues = mutableListOf<String>()
        notAllowedFiles.forEach { file ->
            val fileContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8)
            deprecatedPropertyReferenceStrings.forEach { referenceString ->
                if (fileContents.contains(referenceString)) {
                    issues.add("Illegal use of '$referenceString' found in $file")
                }
            }
        }
        issues.forEach { println(it) }
        Assertions.assertEquals(0, issues.size, "One or more issues were found. Please see the log.")
    }
}