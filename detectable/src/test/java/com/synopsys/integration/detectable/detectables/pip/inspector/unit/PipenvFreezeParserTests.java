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
package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipFreeze;
import com.synopsys.integration.detectable.detectables.pipenv.model.PipFreezeEntry;
import com.synopsys.integration.detectable.detectables.pipenv.parser.PipenvFreezeParser;

@UnitTest
public class PipenvFreezeParserTests {
    @Test
    void findsThreeNamesAndVersions() {
        final List<String> pipFreezeText = new ArrayList<>();
        pipFreezeText.add("simple==1");
        pipFreezeText.add("with-dashes==2.0");
        pipFreezeText.add("dots.and-dashes==3.1.2");

        final PipenvFreezeParser pipenvFreezeParser = new PipenvFreezeParser();
        final PipFreeze pipFreeze = pipenvFreezeParser.parse(pipFreezeText);

        Assertions.assertEquals(3, pipFreeze.getEntries().size(), "Pip freeze should have created three entries.");
        assertContains("simple", "1", pipFreeze);
        assertContains("with-dashes", "2.0", pipFreeze);
        assertContains("dots.and-dashes", "3.1.2", pipFreeze);
    }

    private void assertContains(final String name, final String version, final PipFreeze pipFreeze) {
        final Optional<PipFreezeEntry> found = pipFreeze.getEntries().stream()
                                                   .filter(it -> it.getName().equals(name))
                                                   .filter(it -> it.getVersion().equals(version))
                                                   .findFirst();

        Assertions.assertTrue(found.isPresent(), String.format("Could not find pip freeze entry with name '%s' and version '%s'", name, version));
    }
}
