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

import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.detectable.util.GraphCompare;
import com.synopsys.integration.detectable.util.graph.GraphAssert;

@FunctionalTest
public class RubygemsNodePackagerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    void packagerTest() throws MissingExternalIdException {
        final List<String> actualText = FunctionalTestFiles.asListOfStrings("/rubygems/Gemfile.lock");
        final GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph projects = rubygemsNodePackager.parseProjectDependencies(actualText);
        Assert.assertEquals(8, projects.getRootDependencies().size());

        GraphCompare.assertEqualsResource("/rubygems/expectedPackager_graph.json", projects);
    }

    @Test
    void findsAllVersions() throws MissingExternalIdException {
        //Finds all versions of the package not just the first matching architecture.
        final List<String> actualText = FunctionalTestFiles.asListOfStrings("/rubygems/Gemfile-rails.lock");
        final GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph graph = rubygemsNodePackager.parseProjectDependencies(actualText);

        final GraphAssert graphAssert = new GraphAssert(Forge.RUBYGEMS, graph);
        graphAssert.hasNoDependency(createExternalId("nokogiri", ""));
        graphAssert.hasDependency(createExternalId("nokogiri", "1.8.2"));
        graphAssert.hasDependency(createExternalId("nokogiri", "1.8.2-java"));
        graphAssert.hasDependency(createExternalId("nokogiri", "1.8.2-x86-mingw32"));
    }

    private ExternalId createExternalId(final String name, final String version) {
        return externalIdFactory.createNameVersionExternalId(Forge.RUBYGEMS, name, version);
    }
}
