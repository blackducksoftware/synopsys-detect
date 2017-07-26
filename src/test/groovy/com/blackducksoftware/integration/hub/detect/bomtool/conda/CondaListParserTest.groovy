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

import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.google.gson.GsonBuilder

class CondaListParserTest {

    private CondaListParser condaListParser

    @Before
    public void init() {
        condaListParser = new CondaListParser()
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
        String condaInfoJson = IOUtils.toString(this.getClass().getResourceAsStream('/conda/condaInfo.json'), StandardCharsets.UTF_8)
        String condaListJson = IOUtils.toString(this.getClass().getResourceAsStream('/conda/condaListSmall.json'), StandardCharsets.UTF_8)
        Set<DependencyNode> dependencyNodes = condaListParser.parse(condaListJson, condaInfoJson)
        assertEquals(12, dependencyNodes.size())
    }

    @Test
    public void largeParseTest() {
        String condaInfoJson = IOUtils.toString(this.getClass().getResourceAsStream('/conda/condaInfo.json'), StandardCharsets.UTF_8)
        String condaListJson = IOUtils.toString(this.getClass().getResourceAsStream('/conda/condaListLarge.json'), StandardCharsets.UTF_8)
        Set<DependencyNode> dependencyNodes = condaListParser.parse(condaListJson, condaInfoJson)
        assertEquals(233, dependencyNodes.size())
    }
}
