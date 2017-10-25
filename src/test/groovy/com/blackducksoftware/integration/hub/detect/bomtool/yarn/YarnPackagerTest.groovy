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
package com.blackducksoftware.integration.hub.detect.bomtool.yarn

import static org.junit.Assert.*

import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.nameversion.builder.LinkedNameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.metadata.LinkMetadata
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput

class YarnPackagerTest {
    private final YarnPackager yarnPackager = new YarnPackager()
    private final TestUtil testUtil = new TestUtil()

    @Before
    public void init() {
        yarnPackager.nameVersionNodeTransformer = new NameVersionNodeTransformer(new ExternalIdFactory())
    }

    @Test
    public void parseTest() {
        String yarnLockText = testUtil.getResourceAsUTF8String('yarn/yarn.lock')
        def exeOutput = new ExecutableOutput(yarnLockText, '')
        DependencyGraph dependencyGraph = yarnPackager.parse(exeOutput.getStandardOutputAsList())
        DependencyGraphResourceTestUtil.assertGraph('yarn/expected_graph.json', dependencyGraph);
    }

    @Test
    public void getLineLevelTest() {
        assertEquals(1, yarnPackager.getLineLevel('  '))
        assertEquals(1, yarnPackager.getLineLevel('  Test'))
        assertEquals(1, yarnPackager.getLineLevel('  Test  '))
        assertEquals(2, yarnPackager.getLineLevel('    Test  '))
        assertEquals(1, yarnPackager.getLineLevel('   Test  '))
    }

    @Test
    public void cleanFuzzyNameTest() {
        assertEquals('mime-types', yarnPackager.cleanFuzzyName('mime-types@^2.1.12'))
        assertEquals('mime-types', yarnPackager.cleanFuzzyName('mime-types@2.1.12'))
        assertEquals('@insert', yarnPackager.cleanFuzzyName('@insert@2.1.12'))
        assertEquals('@insert', yarnPackager.cleanFuzzyName('"@insert@2.1.12"'))
    }

    @Test
    public void dependencyLineToNameVersionLinkNodeTest() {
        NameVersionNode nameVersionNode1 = yarnPackager.dependencyLineToNameVersionNode('    name version')
        assertEquals('name@version', nameVersionNode1.name)
        assertNull(nameVersionNode1.version)

        NameVersionNode nameVersionNode2 = yarnPackager.dependencyLineToNameVersionNode('name version')
        assertEquals('name@version', nameVersionNode2.name)
        assertNull(nameVersionNode2.version)

        NameVersionNode nameVersionNode3 = yarnPackager.dependencyLineToNameVersionNode('name')
        assertEquals('name', nameVersionNode3.name)
        assertNull(nameVersionNode3.version)

        NameVersionNode nameVersionNode4 = yarnPackager.dependencyLineToNameVersionNode('"@gulp-sourcemaps/identity-map" "1.X"')
        assertEquals('@gulp-sourcemaps/identity-map@1.X', nameVersionNode4.name)
        assertNull(nameVersionNode4.version)
    }

    @Test
    public void lineToNameVersionLinkNodeSingleTest() {
        final def root = new NameVersionNodeImpl(name: 'test')
        final def nameVersionLinkNodeBuilder = new LinkedNameVersionNodeBuilder(root)
        final String line = '"@types/node@^6.0.46":'
        final NameVersionNode result = yarnPackager.lineToNameVersionNode(nameVersionLinkNodeBuilder, root, line)

        assertEquals('@types/node', result.name)

        assertEquals(1, root.children.size())
        assertEquals('@types/node@^6.0.46', root.children[0].name)
        assertNull(root.children[0].version)
        assertEquals(0, root.children[0].children.size())
        assertEquals(result, ((LinkMetadata) root.children[0].metadata).linkNode)
    }

    @Test
    public void lineToNameVersionLinkNodeMultipleTest() {
        final def root = new NameVersionNodeImpl()
        final def nameVersionLinkNodeBuilder = new LinkedNameVersionNodeBuilder(root)
        final String line = 'acorn@^4.0.3, acorn@^4.0.4:'
        final NameVersionNode result = yarnPackager.lineToNameVersionNode(nameVersionLinkNodeBuilder, root, line)

        assertEquals('acorn', result.name)
        assertEquals(2, root.children.size())

        assertEquals('acorn@^4.0.3', root.children[0].name)
        assertNull(root.children[0].version)
        assertEquals(0, root.children[0].children.size())
        assertEquals(result, ((LinkMetadata) root.children[0].metadata).linkNode)

        assertEquals('acorn@^4.0.4', root.children[1].name)
        assertNull(root.children[1].version)
        assertEquals(0, root.children[1].children.size())
        assertEquals(result, ((LinkMetadata) root.children[1].metadata).linkNode)
    }
}