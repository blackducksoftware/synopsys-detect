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
package com.synopsys.integration.detectable.detectables.pip.unit;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.annotations.UnitTest;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvResult;
import com.synopsys.integration.detectable.detectables.pip.parser.PipInspectorTreeParser;

@UnitTest
public class PipInspectorTreeParserTest {
    private PipInspectorTreeParser parser;

    @BeforeEach
    public void init() {
        parser = new PipInspectorTreeParser(new ExternalIdFactory());
    }

    @Test
    public void indendtationAndLineToNodeTest() {
        final List<String> pipInspectorOutput = new ArrayList<>();
        pipInspectorOutput.add("projectName==projectVersionName");
        pipInspectorOutput.add("   appnope==0.1.0");
        pipInspectorOutput.add("   decorator==4.3.0");
        pipInspectorOutput.add("   dj-database-url==0.5.0");
        pipInspectorOutput.add("   Django==1.10.4");
        pipInspectorOutput.add("   ipython==5.1.0");
        pipInspectorOutput.add("       pexpect==4.6.0");
        pipInspectorOutput.add("           ptyprocess==0.6.0");
        pipInspectorOutput.add("       appnope==0.1.0");
        pipInspectorOutput.add("       setuptools==40.0.0");
        pipInspectorOutput.add("       simplegeneric==0.8.1");
        pipInspectorOutput.add("       decorator==4.3.0");
        pipInspectorOutput.add("       pickleshare==0.7.4");
        pipInspectorOutput.add("       traitlets==4.3.2");
        pipInspectorOutput.add("           six==1.11.0");
        pipInspectorOutput.add("           ipython-genutils==0.2.0");
        pipInspectorOutput.add("           decorator==4.3.0");
        pipInspectorOutput.add("       Pygments==2.2.0");
        pipInspectorOutput.add("       prompt-toolkit==1.0.15");
        pipInspectorOutput.add("           six==1.11.0");
        pipInspectorOutput.add("           wcwidth==0.1.7");
        pipInspectorOutput.add("   ipython-genutils==0.2.0");
        pipInspectorOutput.add("   mypackage==5.2.0");
        pipInspectorOutput.add("   pexpect==4.6.0");
        pipInspectorOutput.add("       ptyprocess==0.6.0");
        pipInspectorOutput.add("   pickleshare==0.7.4");
        pipInspectorOutput.add("   prompt-toolkit==1.0.15");
        pipInspectorOutput.add("       six==1.11.0");
        pipInspectorOutput.add("       wcwidth==0.1.7");
        pipInspectorOutput.add("   psycopg2==2.7.5");
        pipInspectorOutput.add("   ptyprocess==0.6.0");
        pipInspectorOutput.add("   Pygments==2.2.0");
        pipInspectorOutput.add("   simplegeneric==0.8.1");
        pipInspectorOutput.add("   six==1.11.0");
        pipInspectorOutput.add("   traitlets==4.3.2");
        pipInspectorOutput.add("       six==1.11.0");
        pipInspectorOutput.add("       ipython-genutils==0.2.0");
        pipInspectorOutput.add("       decorator==4.3.0");
        pipInspectorOutput.add("   wcwidth==0.1.7");

        final Optional<PipenvResult> validParse = parser.parse(pipInspectorOutput, "");
        Assert.assertTrue(validParse.isPresent());
        Assert.assertEquals("projectName", validParse.get().getProjectName());
        Assert.assertEquals("projectVersionName", validParse.get().getProjectVersion());
    }

    @Test
    public void invalidParseTest() {
        final List<String> invalidText = new ArrayList<>();
        invalidText.add("i am not a valid file");
        invalidText.add("the status should be optional.empty()");
        final Optional<PipenvResult> invalidParse = parser.parse(invalidText, "");
        Assert.assertFalse(invalidParse.isPresent());
    }

    @Test
    public void errorTest() {
        final List<String> invalidText = new ArrayList<>();
        invalidText.add(PipInspectorTreeParser.UNKNOWN_PACKAGE_PREFIX + "probably_an_internal_dependency_PY");
        invalidText.add(PipInspectorTreeParser.UNPARSEABLE_REQUIREMENTS_PREFIX + "/not/a/real/path/encrypted/requirements.txt");
        invalidText.add(PipInspectorTreeParser.UNKNOWN_REQUIREMENTS_PREFIX + "/not/a/real/path/requirements.txt");
        final Optional<PipenvResult> invalidParse = parser.parse(invalidText, "");
        Assert.assertFalse(invalidParse.isPresent());
    }
}
