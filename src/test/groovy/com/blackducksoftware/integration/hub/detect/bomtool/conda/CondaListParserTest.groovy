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
package com.blackducksoftware.integration.hub.detect.bomtool.conda

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.google.gson.GsonBuilder

class CondaListParserTest {
    private final CondaListParser condaListParser = new CondaListParser()
    private final TestUtil testUtil = new TestUtil()

    @Before
    public void init() {
        condaListParser.gson = new GsonBuilder().setPrettyPrinting().create()
    }

    @Test
    public void condaListElementToDependencyNodeTransformerTest() {
        final String platform = 'linux'
        final def element = new CondaListElement()
        element.name = 'sampleName'
        element.version = 'sampleVersion'
        element.buildString = 'py36_0'
        DependencyNode dependencyNode = condaListParser.condaListElementToDependencyNodeTransformer(platform, element)

        assertEquals('sampleName', dependencyNode.name)
        assertEquals('sampleVersion-py36_0-linux', dependencyNode.version)
        assertEquals('sampleName=sampleVersion-py36_0-linux', dependencyNode.externalId.createExternalId())
    }

    @Test
    public void smallParseTest() {
        final String condaInfoJson = testUtil.getResourceAsUTF8String('conda/condaInfo.json')
        final String condaListJson = testUtil.getResourceAsUTF8String('conda/condaListSmall.json')
        Set<DependencyNode> dependencyNodes = condaListParser.parse(condaListJson, condaInfoJson)
        testUtil.testJsonResource('conda/condaListSmallExpected.json', dependencyNodes)
    }

    @Test
    public void largeParseTest() {
        String condaInfoJson = testUtil.getResourceAsUTF8String('conda/condaInfo.json')
        String condaListJson = testUtil.getResourceAsUTF8String('conda/condaListLarge.json')
        Set<DependencyNode> dependencyNodes = condaListParser.parse(condaListJson, condaInfoJson)
        testUtil.testJsonResource('conda/condaListLargeExpected.json', dependencyNodes)
    }
}
