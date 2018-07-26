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
package com.blackducksoftware.integration.hub.detect.bomtool.pip;

import java.util.Optional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency;
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory;
import com.blackducksoftware.integration.hub.detect.bomtool.BomToolType;

public class PipInspectorTreeParserTest {
    private PipInspectorTreeParser parser;

    private final String name = "pip";
    private final String version = "1.0.0";
    private final String fullName = name + PipInspectorTreeParser.SEPARATOR + version;
    private final String line1 = PipInspectorTreeParser.INDENTATION + fullName;
    private final String line2 = PipInspectorTreeParser.INDENTATION + PipInspectorTreeParser.INDENTATION + line1;
    private final String line3 = "invalid line";

    @Before
    public void init() {
        parser = new PipInspectorTreeParser(new ExternalIdFactory());
    }

    @Test
    public void getCurrentIndentationTest() {
        final int indentation1 = parser.getCurrentIndentation(line1);
        Assert.assertEquals(1, indentation1);

        final int indentation2 = parser.getCurrentIndentation(line2);
        Assert.assertEquals(3, indentation2);
    }

    @Test
    public void lineToNodeTest() {
        final Dependency validNode1 = parser.lineToDependency(line1);
        Assert.assertEquals(name, validNode1.name);
        Assert.assertEquals(version, validNode1.version);

        final Dependency validNode2 = parser.lineToDependency(line2);
        Assert.assertEquals(validNode1.name, validNode2.name);
        Assert.assertEquals(validNode1.version, validNode2.version);

        final Dependency invalidNode = parser.lineToDependency(line3);
        Assert.assertNull(invalidNode);
    }

    @Test
    public void invalidParseTest() {
        String invalidText = "i am not a valid file" + System.lineSeparator();
        invalidText += "the result should be optional.empty()";
        final Optional<PipParseResult> invalidParse = parser.parse(BomToolType.PIP_INSPECTOR, invalidText, "");
        Assert.assertFalse(invalidParse.isPresent());
    }
}
