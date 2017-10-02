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

import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.dependency.Dependency
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil

class PipInspectorTreeParserTest {

    private PipInspectorTreeParser parser
    private TestUtil testUtil = new TestUtil()

    private String name = 'pip'
    private String version = '1.0.0'
    private String fullName = name + PipInspectorTreeParser.SEPARATOR + version
    private String line1 = PipInspectorTreeParser.INDENTATION + fullName
    private String line2 = PipInspectorTreeParser.INDENTATION.multiply(2) + line1
    private String line3 = 'invalid line'

    @Before
    void init() {
        parser = new PipInspectorTreeParser()
        parser.externalIdFactory = new ExternalIdFactory()
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
        Dependency validNode1 = parser.lineToDependency(line1)
        Assert.assertEquals(name, validNode1.name)
        Assert.assertEquals(version, validNode1.version)
        Assert.assertTrue(validNode1.children.isEmpty())

        Dependency validNode2 = parser.lineToDependency(line2)
        Assert.assertEquals(validNode1.name, validNode2.name)
        Assert.assertEquals(validNode1.version, validNode2.version)
        Assert.assertEquals(validNode1.children, validNode2.children)

        Dependency invalidNode = parser.lineToDependency(line3)
        Assert.assertNull(invalidNode)
    }

    @Test
    void validParseTest() {
        final String name = 'name'
        final String version = 'version'
        final String space = PipInspectorTreeParser.INDENTATION
        final String child1Text = 'apple' + PipInspectorTreeParser.SEPARATOR + '5.3.2'
        final String child2Text = 'orange' + PipInspectorTreeParser.SEPARATOR + '4.3.1'
        final String child3Text = 'pear' + PipInspectorTreeParser.SEPARATOR + '9.8.7'
        final String validText = """
${PipInspectorTreeParser.UNKNOWN_REQUIREMENTS_PREFIX} reqs.txt
${PipInspectorTreeParser.UNKNOWN_PACKAGE_PREFIX} UnkownPackageName

${name + PipInspectorTreeParser.SEPARATOR + version}
${space + child1Text}
${space*2 + child3Text}
${space + child2Text}
${space + child3Text}
"""

        Dependency root = parser.parse(validText)
        ExternalId expectedExternalId = parser.externalIdFactory.createNameVersionExternalId(Forge.PYPI, 'name', 'version')
        Assert.assertEquals('name', root.name)
        Assert.assertEquals('version', root.version)
        testUtil.testJson(expectedExternalId.toString(), root.externalId.toString())
        Assert.assertEquals(3, root.children.size())
    }

    @Test
    void invalidParseTest() {
        def invalidText = """
        i am not a valid file
        the result should be null
        """
        Dependency root = parser.parse(invalidText)
        Assert.assertNull(root)
    }
}
