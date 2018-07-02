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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan


import static org.junit.Assert.*

import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

class CpanListParserTest {
    private final TestUtil testUtil = new TestUtil()
    private final CpanListParser cpanListParser = new CpanListParser(new ExternalIdFactory())

    private final List<String> cpanListText = testUtil.getResourceAsUTF8String('/cpan/cpanList.txt').split('\n').toList()
    private final List<String> showDepsText = testUtil.getResourceAsUTF8String('/cpan/showDeps.txt').split('\n').toList()

    @Test
    public void parseTest() {
        String cpanList = '''
Test::More\t1.2.3
Test::Less\t1.2.4
This is an invalid line
This\t1\t1also\t1invalid
Invalid
'''
        Map<String, String> nodeMap = cpanListParser.createNameVersionMap(cpanList.tokenize('\n'))
        assertEquals(2, nodeMap.size())
        assertNotNull(nodeMap['Test::More'])
        assertNotNull(nodeMap['Test::Less'])
        assertEquals('1.2.3', nodeMap['Test::More'])
        assertEquals('1.2.4', nodeMap['Test::Less'])
    }

    @Test
    public void getDirectModuleNamesTest() {
        List<String> names = cpanListParser.getDirectModuleNames(showDepsText)
        assertEquals(4, names.size())
        assertTrue(names.contains('ExtUtils::MakeMaker'))
        assertTrue(names.contains('Test::More'))
        assertTrue(names.contains('perl'))
        assertTrue(names.contains('ExtUtils::MakeMaker'))
    }

    @Test
    public void makeDependencyNodesTest() {
        DependencyGraph dependencyGraph = cpanListParser.parse(cpanListText, showDepsText)

        DependencyGraphResourceTestUtil.assertGraph('/cpan/expectedDependencyNodes_graph.json', dependencyGraph)
    }
}
