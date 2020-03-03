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
public class GemlockNodeParserTest {

    @Test
    public void testParsingEqualsGemfileLock() throws MissingExternalIdException {
        final List<String> gemfileLockContents = Arrays.asList(
            "GEM",
            "  remote: https://artifactory.ilabs.io/artifactory/api/gems/gem-public/",
            "  specs:",
            "    SyslogLogger (2.0)",
            "    activesupport (4.1.15)",
            "      json (~> 1.7, >= 1.7.7)",
            "    json (1.8.6)",
            "",
            "PLATFORMS",
            "  ruby",
            "",
            "DEPENDENCIES",
            "  SyslogLogger (~> 2.0)",
            "  activesupport (~> 4.1.15)"
        );
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("activesupport", "4.1.15");
        graphAssert.hasRootDependency("SyslogLogger", "2.0");
        graphAssert.hasParentChildRelationship("activesupport", "4.1.15", "json", "1.8.6");
    }

    @Test
    public void testMissingVersionsGemfileLock() throws MissingExternalIdException {
        final List<String> gemfileLockContents = Arrays.asList(
            "GEM",
            "  remote: https://artifactory.rds.lexmark.com/artifactory/api/gems/rubygems/",
            "  remote: https://artifactory.rds.lexmark.com/artifactory/api/gems/enterprise-gem-IDP/",
            "  specs:",
            "    activesupport (4.2.7.1)",
            "      thread_safe (~> 0.3, >= 0.3.4)",
            "      tzinfo (~> 1.1)",
            "    bullet (5.5.0)",
            "      activesupport (>= 3.0.0)",
            "      uniform_notifier (~> 1.10.0)",
            "    devise (4.2.1)",
            "    thread_safe (0.3.6-java)",
            "    tzinfo (1.2.3)",
            "      thread_safe (~> 0.1)",
            "    uniform_notifier (1.10.0)",
            "",
            "PLATFORMS",
            "  java",
            "",
            "DEPENDENCIES",
            "  bullet (~> 5.5.0)",
            "  devise (~> 4.2.1)"
        );
        final GemlockParser gemlockNodeParser = new GemlockParser(new ExternalIdFactory());
        final DependencyGraph dependencyGraph = gemlockNodeParser.parseProjectDependencies(gemfileLockContents);

        NameVersionGraphAssert graphAssert = new NameVersionGraphAssert(Forge.RUBYGEMS, dependencyGraph);
        graphAssert.hasRootSize(2);
        graphAssert.hasRootDependency("bullet", "5.5.0");
        graphAssert.hasRootDependency("devise", "4.2.1");
        graphAssert.hasParentChildRelationship("bullet", "5.5.0", "activesupport", "4.2.7.1");
        graphAssert.hasParentChildRelationship("activesupport", "4.2.7.1", "tzinfo", "1.2.3");
        graphAssert.hasParentChildRelationship("tzinfo", "1.2.3", "thread_safe", "0.3.6-java");
        graphAssert.hasParentChildRelationship("bullet", "5.5.0", "uniform_notifier", "1.10.0");
    }
}
