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
package com.synopsys.integration.detectable.detectables.rubygems.gemspec.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecDependency;
import com.synopsys.integration.detectable.detectables.rubygems.gemspec.parse.GemspecLineParser;

@UnitTest
public class GemspecLineParserTest {
    private final GemspecLineParser gemspecLineParser = new GemspecLineParser();

    @Test
    void shouldParseLines() {
        final String line = "gem.add_dependency \"fakegem\", \">= 1.0.0\"";
        assertTrue(gemspecLineParser.shouldParseLine(line));

        final String nonDependencyLine = "Some other line";
        assertFalse(gemspecLineParser.shouldParseLine(nonDependencyLine));
    }

    @Test
    void handleErrors() {
        final String line = "gem.add_dependency";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertFalse(gemspecDependency.isPresent());

        final Optional<GemspecDependency> gemspecDependencyNull = gemspecLineParser.parseLine(null);
        assertFalse(gemspecDependencyNull.isPresent());
    }

    @Test
    void parseDoubleQuotes() {
        final String line = "gem.add_dependency \"fakegem\", \">= 1.0.0\"";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseSingleQuotes() {
        final String line = "gem.add_dependency 'fakegem', '>= 1.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithDifferentPrefixTest() {
        final String line = "s.add_dependency 'fakegem.rb', '>= 1.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithFileExtension() {
        final String line = "gem.add_dependency 'fakegem.rb', '>= 1.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithoutVersion() {
        final String line = "gem.add_dependency 'fakegem'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertFalse(gemspecDependency.get().getVersion().isPresent());
    }

    @Test
    void parseGemWithMultipleVersions() {
        final String line = "gem.add_dependency 'fakegem', '>= 1.0.0', '<2.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0, <2.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithSingleElementArray() {
        final String line = "gem.add_dependency 'fakegem', ['>= 1.0.0']";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithMultipleElementArray() {
        final String line = "gem.add_dependency 'fakegem', ['>= 1.0.0', '<2.0.0']";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0, <2.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseRuntimeDependency() {
        final String line = "gem.add_runtime_dependency 'fakegem', '>= 1.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseDevelopmentDependency() {
        final String line = "gem.add_dependency 'fakegem', '>= 1.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithParentheses() {
        final String line = "gem.add_development_dependency('fakegem', '>= 1.0.0')";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithWeirdCharacters() {
        final String line = "gem.add_dependency(%q<fakegem>, '>= 1.0.0'";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }

    @Test
    void parseGemWithInlineComment() {
        final String line = "gem.add_dependency 'fakegem', '>= 1.0.0' # I am an inline comment";
        final Optional<GemspecDependency> gemspecDependency = gemspecLineParser.parseLine(line);
        assertTrue(gemspecDependency.isPresent());
        assertEquals("fakegem", gemspecDependency.get().getName());
        assertTrue(gemspecDependency.get().getVersion().isPresent());
        assertEquals(">= 1.0.0", gemspecDependency.get().getVersion().get());
    }
}
