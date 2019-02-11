/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.synopsys.integration.detectable.detectables.pip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectables.pip.model.PipParseResult;
import com.synopsys.integration.detectable.detectables.pip.parser.PipInspectorTreeParser;

public class PipInspectorTreeParserTest {
    private PipInspectorTreeParser parser;

    @Before
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

        final Optional<PipParseResult> validParse = parser.parse(pipInspectorOutput, "");
        Assert.assertTrue(validParse.isPresent());
        Assert.assertTrue(validParse.get().getProjectName().equals("projectName"));
        Assert.assertTrue(validParse.get().getProjectVersion().equals("projectVersionName"));
    }

    @Test
    public void invalidParseTest() {
        final List<String> invalidText = new ArrayList<>();
        invalidText.add("i am not a valid file");
        invalidText.add("the status should be optional.empty()");
        final Optional<PipParseResult> invalidParse = parser.parse(invalidText, "");
        Assert.assertFalse(invalidParse.isPresent());
    }

    @Test
    public void errorTest() {
        final List<String> invalidText = new ArrayList<>();
        invalidText.add(PipInspectorTreeParser.UNKNOWN_PACKAGE_PREFIX + "probably_an_internal_dependency_PY");
        invalidText.add(PipInspectorTreeParser.UNPARSEABLE_REQUIREMENTS_PREFIX + "/not/a/real/path/encrypted/requirements.txt");
        invalidText.add(PipInspectorTreeParser.UNKNOWN_REQUIREMENTS_PREFIX + "/not/a/real/path/requirements.txt");
        final Optional<PipParseResult> invalidParse = parser.parse(invalidText, "");
        Assert.assertFalse(invalidParse.isPresent());
    }
}
