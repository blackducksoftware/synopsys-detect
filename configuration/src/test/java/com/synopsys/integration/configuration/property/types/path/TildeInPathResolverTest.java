/**
 * configuration
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
package com.synopsys.integration.configuration.property.types.path;

import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;

public class TildeInPathResolverTest {
    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTilde() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        final Path resolved = resolver.resolvePath("~/Documents/source/funtional/detect");

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals("/Users/ekerwin/Documents/source/funtional/detect", resolved.toString(), "Tilde's should be resolved on Unix operating systems.");
    }

    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTildeInTheMiddleOfAPath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        final String filePath = "/Documents/~source/~/funtional/detect";
        final Path resolved = resolver.resolvePath(filePath);

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals(filePath, resolved.toString(), "Tilde's in the middle of the path should not be resolved.");
    }

    @Test
    public void testBlankPath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Assertions.assertEquals("", resolver.resolvePath("").toString());
    }

    @Test
    @EnabledOnOs(WINDOWS) // Path is more forgiving of whitespace on Unix systems.
    public void testWhitespacePath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Assertions.assertThrows(InvalidPathException.class, () -> resolver.resolvePath("  "));
    }
}
