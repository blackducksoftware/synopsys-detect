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
package com.synopsys.integration.detectable.detectables.rubygems.gemlock.functional;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;

@FunctionalTest
public class GemlockNodeParserTest {
    @Test
    public void testParsingSmallGemfileLock() throws MissingExternalIdException {
        final String text = FunctionalTestFiles.asString("/rubygems/small_gemfile_lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        GraphCompare.assertEqualsResource("/rubygems/expectedSmallParser_graph.json", dependencyGraph);
    }

    @Test
    public void testParsingGemfileLock() throws MissingExternalIdException {
        final String text = FunctionalTestFiles.asString("/rubygems/Gemfile.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        GraphCompare.assertEqualsResource("/rubygems/expectedParser_graph.json", dependencyGraph);
    }

    @Test
    public void testParsingEqualsGemfileLock() throws MissingExternalIdException {
        final String text = FunctionalTestFiles.asString("/rubygems/Gemfile_equals_version.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        final Dependency bundler = dependencyGraph.getDependency(new ExternalIdFactory().createNameVersionExternalId(Forge.RUBYGEMS, "bundler", "1.11.2"));
        assertNotNull(bundler);
    }

    @Test
    public void testMissingVersionsGemfileLock() throws MissingExternalIdException {
        final String text = FunctionalTestFiles.asString("/rubygems/Gemfile_missing_versions.lock");
        final List<String> gemfileLockContents = Arrays.asList(text.split("\n"));
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        final Dependency newrelic_rpm = dependencyGraph.getDependency(new ExternalIdFactory().createNameVersionExternalId(Forge.RUBYGEMS, "newrelic_rpm", ""));
        assertNotNull(newrelic_rpm);
    }
}
