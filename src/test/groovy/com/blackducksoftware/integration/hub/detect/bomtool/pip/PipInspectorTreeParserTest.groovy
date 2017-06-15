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
package com.blackducksoftware.integration.hub.detect.bomtool.pip

import org.junit.Assert
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer

class PipInspectorTreeParserTest {

    private PipInspectorTreeParser parser
    private NameVersionNodeTransformer nameVersionNodeTransformer

    private String name = 'pip'
    private String version = '1.0.0'
    private String fullName = name + PipInspectorTreeParser.SEPERATOR + version
    private String line1 = PipInspectorTreeParser.INDENTATION + fullName
    private String line2 = PipInspectorTreeParser.INDENTATION.multiply(2) + line1
    private String line3 = 'invalid line'

    @Before
    void init() {
        parser = new PipInspectorTreeParser()
        nameVersionNodeTransformer = new NameVersionNodeTransformer()
    }

    @Test
    void getCurrentIndentationTest() {
        int indentation1 = parser.getCurrentIndentation(line1)
        Assert.assertEquals(1, indentation1)

        int indentation2 = parser.getCurrentIndentation(line2)
        Assert.assertEquals(3, indentation2)
    }

    @Test
    void lineToNodeTest() {
        NameVersionNode validNode1 = parser.lineToNode(line1)
        Assert.assertEquals(name, validNode1.name)
        Assert.assertEquals(version, validNode1.version)
        Assert.assertTrue(validNode1.children.isEmpty())

        NameVersionNode validNode2 = parser.lineToNode(line2)
        Assert.assertEquals(validNode1.name, validNode2.name)
        Assert.assertEquals(validNode1.version, validNode2.version)
        Assert.assertEquals(validNode1.children, validNode2.children)

        NameVersionNode invalidNode = parser.lineToNode(line3)
        Assert.assertNull(invalidNode)
    }

    @Test
    void validParseTest() {
        def space = PipInspectorTreeParser.INDENTATION
        def seperator = PipInspectorTreeParser.SEPERATOR
        def child1Name = 'apple'
        def child1Version = '5.3.2'
        def child1Full = child1Name + seperator + child1Version
        def child2Name = 'orange'
        def child2Version = '4.3.1'
        def child2Full = child2Name + seperator + child2Version
        def validText = """
        ${fullName}
        ${space + child1Full}
        ${space + child2Full}
        """

        DependencyNode root = parser.parse(nameVersionNodeTransformer, validText)
        ExternalId expectedExternalId = new NameVersionExternalId(Forge.PYPI, name, version)
        Assert.assertEquals(name, root.name)
        Assert.assertEquals(version, root.version)
        Assert.assertEquals(expectedExternalId, root.externalId)

        Assert.assertEquals(2, root.children.size())
        int foundCount = 0
        for(DependencyNode child : root.children) {
            if (child.name == child1Name || child.name == child2Name) {
                foundCount++
            }
        }
        Assert.assertEquals(2, foundCount)
    }

    @Test
    void invalidParseTest() {
        def invalidText = """
        i am not a valid file
        the result should be null
        """
        DependencyNode root = parser.parse(nameVersionNodeTransformer, invalidText)
        Assert.assertNull(root)
    }
}
