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

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.FunctionalTest;
import com.synopsys.integration.detectable.detectables.rubygems.gemlock.parse.GemlockParser;
import com.synopsys.integration.detectable.util.graph.NameVersionGraphAssert;

@FunctionalTest
public class RubygemsNodePackagerTest {
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Test
    void findsAllVersions() throws MissingExternalIdException {
        //Finds all versions of the package not just the first matching architecture.
        final List<String> actualText = Arrays.asList(
            "GEM",
            "  remote: https://rubygems.org/",
            "  specs:",
            "    nokogiri (1.8.2)",
            "      mini_portile2 (~> 2.3.0)",
            "    nokogiri (1.8.2-java)",
            "    nokogiri (1.8.2-x86-mingw32)",
            "    nokoparent (3.1.0)",
            "      nokogiri (~> 1.8)",
            "",
            "PLATFORMS",
            "  java",
            "  ruby",
            "  x86-mingw32",
            "",
            "DEPENDENCIES",
            "  nokoparent (>= 1)",
            "  nokogiri (>= 1.8.1)"
        );
        final GemlockParser rubygemsNodePackager = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph graph = rubygemsNodePackager.parseProjectDependencies(actualText);

        final NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, graph);

        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("nokoparent", "3.1.0");
        graphAssert.hasRootDependency("nokogiri", "1.8.2");
        graphAssert.hasNoDependency("nokogiri", "");
        graphAssert.hasDependency("nokogiri", "1.8.2");
        graphAssert.hasDependency("nokogiri", "1.8.2-java");
        graphAssert.hasDependency("nokogiri", "1.8.2-x86-mingw32");
        graphAssert.hasParentChildRelationship("nokoparent", "3.1.0", "nokogiri", "1.8.2");
        graphAssert.hasParentChildRelationship("nokogiri", "1.8.2", "mini_portile2", "");
    }

}
