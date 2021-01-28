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
package com.synopsys.integration.detectable.detectables.yarn.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLock;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockParserNew;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntry;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryId;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.YarnLockEntryParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.element.YarnLockDependencySpecParser;
import com.synopsys.integration.detectable.detectables.yarn.parse.entry.element.YarnLockEntryElementParser;

@UnitTest
public class YarnLockParserTest {
    @Test
    void testThatYarnParsesSemiColon() {
        List<String> yarnLockText = Arrays.asList(
            "any-root-dep@1:",
            "  version: 1.0.0", //must have a version to create an entry
            "  dependencies:",
            "    some-peer: ^10.0.0"
        );
        YarnLockParserNew yarnLockParser = createYarnLockParser();

        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        Assertions.assertEquals(1, yarnLock.getEntries().size());
        YarnLockEntry first = yarnLock.getEntries().get(0);
        Assertions.assertEquals("1.0.0", first.getVersion());
        Assertions.assertEquals(1, first.getDependencies().size());
        YarnLockDependency dep = first.getDependencies().get(0);

        Assertions.assertEquals("some-peer", dep.getName());
        Assertions.assertEquals("^10.0.0", dep.getVersion());
        Assertions.assertFalse(dep.isOptional());
    }

    @Test
    void testThatYarnLockPeerNotAdded() {
        List<String> yarnLockText = Arrays.asList(
            "any-root-dep@1:",
            "  version: 1", //must have a version to create an entry
            "  peerDependencies:",
            "    some-peer: ^10.0.0",
            "  peerDependenciesMeta:",
            "    some-peer:",
            "      optional: true"
        );

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        Assertions.assertEquals(1, yarnLock.getEntries().size());
        YarnLockEntry first = yarnLock.getEntries().get(0);
        Assertions.assertEquals(0, first.getDependencies().size());
    }

    @Test
    void testOptionalSetFromMeta() {
        List<String> yarnLockText = Arrays.asList(
            "any-root-dep@1:",
            "  version: 1", //must have a version to create an entry
            "  dependencies:",
            "    should-be-optional: 1.0.0",
            "    should-not-be-optional: 2.0.0",
            "  dependenciesMeta:",
            "    should-be-optional:",
            "      optional: true"
        );

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);
        Assertions.assertTrue(yarnLock.getEntries().size() > 0);

        YarnLockEntry first = yarnLock.getEntries().get(0);
        YarnLockDependency optDep = first.getDependencies().stream().filter(it -> it.getName().equals("should-be-optional")).findFirst().get();
        YarnLockDependency reqDep = first.getDependencies().stream().filter(it -> it.getName().equals("should-not-be-optional")).findFirst().get();

        Assertions.assertTrue(optDep.isOptional());
        Assertions.assertFalse(reqDep.isOptional());
    }

    @Test
    void testThatYarnLockIsParsedCorrectlyToMap() {
        List<String> yarnLockText = new ArrayList<>();
        yarnLockText.add("# THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY.");
        yarnLockText.add("# yarn lockfile v1");
        yarnLockText.add("");
        yarnLockText.add("");
        yarnLockText.add("async@0.9.0:");
        yarnLockText.add("  version \"0.9.0\"");
        yarnLockText.add("  resolved \"http://nexus.fr.murex.com/nexus3/repository/npm-all/async/-/async-0.9.0.tgz#ac3613b1da9bed1b47510bb4651b8931e47146c7\"");
        yarnLockText.add("colors@~1.0.3:");
        yarnLockText.add("  version \"1.0.3\"");
        yarnLockText.add("  resolved \"http://nexus.fr.murex.com/nexus3/repository/npm-all/colors/-/colors-1.0.3.tgz#0433f44d809680fdeb60ed260f1b0c262e82a40b\"");

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        assertEntry(yarnLock, "async", "0.9.0", "0.9.0");
        assertEntry(yarnLock, "colors", "~1.0.3", "1.0.3");
    }

    @Test
    void testParsingNamesWithAtSymbolSlash() {
        List<String> yarnLockText = new ArrayList<>();
        yarnLockText.add("# THIS IS AN AUTOGENERATED FILE. DO NOT EDIT THIS FILE DIRECTLY.");
        yarnLockText.add("# yarn lockfile v1");
        yarnLockText.add("");
        yarnLockText.add("");
        yarnLockText.add("\"@apollo/client@^3.1.3\", \"@apollo/client@^3.1.4\", \"@apollo/client@^3.3.6\":\n");
        yarnLockText.add("  version \"3.3.6\"\n");
        yarnLockText.add("  resolved \"https://registry.yarnpkg.com/@apollo/client/-/client-3.3.6.tgz#f359646308167f38d5bc498dfc2344c888400093\"\n");
        yarnLockText.add("  integrity sha512-XSm/STyNS8aHdDigLLACKNMHwI0qaQmEHWHtTP+jHe/E1wZRnn66VZMMgwKLy2V4uHISHfmiZ4KpUKDPeJAKqg==\n");
        yarnLockText.add("  dependencies:\n");
        yarnLockText.add("    \"@graphql-typed-document-node/core\" \"^3.0.0\"\n");

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        YarnLockDependency dep = new YarnLockDependency("@graphql-typed-document-node/core", "^3.0.0", false);
        assertEntry(yarnLock, "@apollo/client", "^3.1.3", "3.3.6", dep);
    }

    @Test
    void testThatYarnLockVersionsResolveAsExpected() {
        List<String> yarnLockText = new ArrayList<>();
        yarnLockText.add("http-proxy@^1.8.1:");
        yarnLockText.add("  version \"1.16.2\"");
        yarnLockText.add("  resolved \"http://nexus.fr.murex.com/nexus3/repository/npm-all/http-proxy/-/http-proxy-1.16.2.tgz#06dff292952bf64dbe8471fa9df73066d4f37742\"");
        yarnLockText.add("  dependencies:");
        yarnLockText.add("    eventemitter3 \"1.x.x\"");
        yarnLockText.add("    requires-port \"1.x.x\"");
        yarnLockText.add("http-server@^0.9.0:");
        yarnLockText.add("  version \"0.9.0\"");
        yarnLockText.add("  resolved \"http://nexus.fr.murex.com/nexus3/repository/npm-all/http-server/-/http-server-0.9.0.tgz#8f1b06bdc733618d4dc42831c7ba1aff4e06001a\"");

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        assertEntry(yarnLock, "http-proxy", "^1.8.1", "1.16.2", new YarnLockDependency("eventemitter3", "1.x.x", false), new YarnLockDependency("requires-port", "1.x.x", false));
        assertEntry(yarnLock, "http-server", "^0.9.0", "0.9.0");
    }

    @Test
    void testThatMultipleDepsPerLineCanBeHandledCorrectly() {
        List<String> yarnLockText = new ArrayList<>();
        yarnLockText.add("debug@2, debug@2.6.9, debug@^2.2.0, debug@^2.3.3, debug@~2.6.4, debug@~2.6.6:");
        yarnLockText.add("  version \"2.6.9\"");
        yarnLockText.add("  resolved \"http://nexus/nexus3/repository/npm-all/debug/-/debug-2.6.9.tgz#5d128515df134ff327e90a4c93f4e077a536341f\"");
        yarnLockText.add("  dependencies:");
        yarnLockText.add("    ms \"2.0.0\"");

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        assertEntry(yarnLock, "debug", "2", "2.6.9", new YarnLockDependency("ms", "2.0.0", false));
        assertEntry(yarnLock, "debug", "2.6.9", "2.6.9", new YarnLockDependency("ms", "2.0.0", false));
        assertEntry(yarnLock, "debug", "^2.2.0", "2.6.9", new YarnLockDependency("ms", "2.0.0", false));
        assertEntry(yarnLock, "debug", "^2.3.3", "2.6.9", new YarnLockDependency("ms", "2.0.0", false));
        assertEntry(yarnLock, "debug", "~2.6.4", "2.6.9", new YarnLockDependency("ms", "2.0.0", false));
        assertEntry(yarnLock, "debug", "~2.6.6", "2.6.9", new YarnLockDependency("ms", "2.0.0", false));

    }

    @Test
    void testThatDependenciesWithQuotesAreResolvedCorrectly() {
        List<String> yarnLockText = new ArrayList<>();
        yarnLockText.add("\"cssstyle@>= 0.2.37 < 0.3.0\":");
        yarnLockText.add("  version \"0.2.37\"");
        yarnLockText.add("  resolved \"http://nexus/nexus3/repository/npm-all/cssstyle/-/cssstyle-0.2.37.tgz#541097234cb2513c83ceed3acddc27ff27987d54\"");
        yarnLockText.add("  dependencies:");
        yarnLockText.add("    cssom \"0.3.x\"");

        YarnLockParserNew yarnLockParser = createYarnLockParser();
        YarnLock yarnLock = yarnLockParser.parseYarnLock(yarnLockText);

        assertEntry(yarnLock, "cssstyle", ">= 0.2.37 < 0.3.0", "0.2.37", new YarnLockDependency("cssom", "0.3.x", false));
    }

    void assertEntry(YarnLock yarnLock, String idName, String idVersion, String resolvedVersion, YarnLockDependency... dependencies) {
        boolean found = false;
        for (YarnLockEntry entry : yarnLock.getEntries()) {
            for (YarnLockEntryId entryId : entry.getIds()) {
                if (entryId.getName().equals(idName) && entryId.getVersion().equals(idVersion)) {
                    found = true;
                    assertEquals(resolvedVersion, entry.getVersion(), "Yarn entry should have found correct resolved version.");
                    assertEquals(dependencies.length, entry.getDependencies().size(), "Yarn entry should have found correct number of dependencies.");
                    for (YarnLockDependency dependency : dependencies) {
                        boolean dFound = false;
                        for (YarnLockDependency entryDependency : entry.getDependencies()) {
                            if (entryDependency.getName().equals(dependency.getName()) && entryDependency.getVersion().equals(dependency.getVersion()) && entryDependency.isOptional() == dependency.isOptional()) {
                                dFound = true;
                            }
                        }
                        assertTrue(dFound, "Could not find yarn dependency for entry " + idName + " with name " + dependency.getName() + " and version " + dependency.getVersion() + " and optional " + dependency.isOptional() + ".");
                    }
                }
            }
        }
        assertTrue(found, "Could not find yarn lock entry with name " + idName + " and version " + idVersion + ".");
    }

    @NotNull
    private YarnLockParserNew createYarnLockParser() {
        YarnLockLineAnalyzer lineAnalyzer = new YarnLockLineAnalyzer();
        YarnLockDependencySpecParser yarnLockDependencySpecParser = new YarnLockDependencySpecParser(lineAnalyzer);
        YarnLockEntryElementParser yarnLockEntryElementParser = new YarnLockEntryElementParser(lineAnalyzer, yarnLockDependencySpecParser);
        YarnLockEntryParser entryParser = new YarnLockEntryParser(lineAnalyzer, yarnLockEntryElementParser);
        YarnLockParserNew yarnLockParser = new YarnLockParserNew(entryParser);
        return yarnLockParser;
    }
}
