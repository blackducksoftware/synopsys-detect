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
package com.synopsys.integration.detect.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.common.util.Bds;
import com.synopsys.integration.configuration.property.Property;

@Disabled // For now these tests are not actually helping anyone. Will be tagged with "lint" in the future.
public class DeprecatedPropertyReferenceTest {
    private List<String> fileTypesToCheck = Bds.listOf(
        "java",
        "kt",
        "groovy",
        "kts"
    );

    private List<String> excludedFileNames = Bds.of(Bds.listOf(
        DetectConfigurationFactory.class,
        DetectableOptionFactory.class,
        DetectProperties.class,
        this.getClass()
    )).map(Class::getSimpleName).toList();

    private List<String> deprecatedPropertyReferenceStrings = Bds.of(DetectProperties.allProperties().getProperties())
                                                                  .filter(it -> it.getPropertyDeprecationInfo() != null)
                                                                  .map(Property::getKey)
                                                                  .map(String::toUpperCase)
                                                                  .map(key -> key.replace(".", "_"))
                                                                  .toList();

    public DeprecatedPropertyReferenceTest() throws IllegalAccessException {
    }

    @Test
    public void testCodeReferencesToDeprecatedProperties() throws IOException {
        File rootDir = new File("src");
        Collection<File> sourceFiles = FileUtils.listFiles(rootDir, fileTypesToCheck.toArray(ArrayUtils.EMPTY_STRING_ARRAY), true);
        List<File> notAllowedFiles = Bds.of(sourceFiles)
                                         .filter(file -> !excludedFileNames.contains(FilenameUtils.getBaseName(file.getName())))
                                         .toList();

        List<String> issues = Bds.listOf();
        for (File file : notAllowedFiles) {
            String fileContents = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            for (String referenceString : deprecatedPropertyReferenceStrings) {
                if (fileContents.contains(referenceString)) {
                    issues.add("Illegal use of '$referenceString' found in $file");
                }
            }
        }
        issues.forEach(System.out::println);
        Assertions.assertEquals(0, issues.size(), "One or more issues were found. Please see the log.");
    }
}