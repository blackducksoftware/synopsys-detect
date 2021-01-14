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
package com.synopsys.integration.detectable.detectables.pear.unit;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pear.model.PackageDependency;
import com.synopsys.integration.detectable.detectables.pear.parse.PearPackageDependenciesParser;
import com.synopsys.integration.exception.IntegrationException;

@UnitTest
class PearPackageDependenciesParserTest {
    private static PearPackageDependenciesParser pearPackageDependenciesParser;

    @BeforeEach
    void setUp() {
        pearPackageDependenciesParser = new PearPackageDependenciesParser();
    }

    @Test
    void parseValid() throws IntegrationException {
        final List<String> validLines = Arrays.asList(
            "=====================",
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package        pear/Structures_Graph    (recommended version 1.1.1)"
        );

        final List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(validLines);
        Assertions.assertEquals(2, packageDependencies.size());
        Assertions.assertEquals("Archive_Tar", packageDependencies.get(0).getName());
        Assertions.assertTrue(packageDependencies.get(0).isRequired());
        Assertions.assertEquals("Structures_Graph", packageDependencies.get(1).getName());
        Assertions.assertFalse(packageDependencies.get(1).isRequired());
    }

    @Test
    void parseNoStart() throws IntegrationException {
        final List<String> noStartLines = Arrays.asList(
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package        pear/Structures_Graph    (recommended version 1.1.1)"
        );

        final List<PackageDependency> packageDependencies = pearPackageDependenciesParser.parse(noStartLines);
        Assertions.assertEquals(0, packageDependencies.size());
    }

    @Test
    void parseMissingInfo() {
        final List<String> missingInfoLines = Arrays.asList(
            "=====================",
            "Required? Type           Name                    Versioning                   Group",
            "Yes       Php                                     (version >= 5.4.0)",
            "Yes       Pear Installer                          (version >= 1.9.0)",
            "Yes       Package        pear/Archive_Tar         (recommended version 1.4.3)",
            "No       Package"
        );

        Assertions.assertThrows(IntegrationException.class, () -> pearPackageDependenciesParser.parse(missingInfoLines));
    }
}