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
package com.blackducksoftware.integration.hub.detect.detector.cpan;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.springframework.util.StringUtils;

import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil;
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil;
import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class CpanListParserTest {
    private final TestUtil testUtil = new TestUtil();
    private final CpanListParser cpanListParser = new CpanListParser(new ExternalIdFactory());

    private final List<String> cpanListText = Arrays.asList(testUtil.getResourceAsUTF8String("/cpan/cpanList.txt").split("\n"));
    private final List<String> showDepsText = Arrays.asList(testUtil.getResourceAsUTF8String("/cpan/showDeps.txt").split("\n"));

    @Test
    public void parseTest() {
        String cpanList = "Test::More\t1.2.3" + "\n";
        cpanList += "Test::Less\t1.2.4" + "\n";
        cpanList += "This is an invalid line" + "\n";
        cpanList += "This\t1\t1also\t1invalid" + "\n";
        cpanList += "Invalid" + "\n";

        final List<String> tokens = Arrays.asList(StringUtils.tokenizeToStringArray(cpanList, "\n"));
        final Map<String, String> nodeMap = cpanListParser.createNameVersionMap(tokens);
        assertEquals(2, nodeMap.size());
        assertNotNull(nodeMap.get("Test::More"));
        assertNotNull(nodeMap.get("Test::Less"));
        assertEquals("1.2.3", nodeMap.get("Test::More"));
        assertEquals("1.2.4", nodeMap.get("Test::Less"));
    }

    @Test
    public void getDirectModuleNamesTest() {
        final List<String> names = cpanListParser.getDirectModuleNames(showDepsText);
        assertEquals(4, names.size());
        assertTrue(names.contains("ExtUtils::MakeMaker"));
        assertTrue(names.contains("Test::More"));
        assertTrue(names.contains("perl"));
        assertTrue(names.contains("ExtUtils::MakeMaker"));
    }

    @Test
    public void makeDependencyNodesTest() {
        final DependencyGraph dependencyGraph = cpanListParser.parse(cpanListText, showDepsText);

        DependencyGraphResourceTestUtil.assertGraph("/cpan/expectedDependencyNodes_graph.json", dependencyGraph);
    }
}
