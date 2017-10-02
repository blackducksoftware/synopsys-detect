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

import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.Forge
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.testutils.BdioCreationUtil

class CpanBomToolTest {
    private final CpanPackager cpanPackager = new CpanPackager()
    private final BdioCreationUtil bdioCreationUtil = new BdioCreationUtil()

    private final String sourcePath = '~/Downloads/grcpan'
    private final String cpanListOutputPath = "${sourcePath}/cpan-l-out.txt"
    private final String cpanmShowDepsOutputPath = "${sourcePath}/cpanm-showdeps-out.txt"
    private final String outputFilePath = "${sourcePath}/testOutput.jsonld"
    private final ExternalIdFactory externalIdFactory = new ExternalIdFactory();

    @Before
    public void init() {
        cpanPackager.cpanListParser = new CpanListParser()
        cpanPackager.nameVersionNodeTransformer = new NameVersionNodeTransformer()
    }

    @Test
    public void makeDependencyNodesExternalTest() {
        final String cpanListText = new File(cpanListOutputPath).text
        final String showDepsText = new File(cpanmShowDepsOutputPath).text

        DependencyGraph dependencyGraph = cpanPackager.makeDependencyGraph(cpanListText, showDepsText)

        ExternalId externalId = externalIdFactory.createPathExternalId(Forge.CPAN, sourcePath)
        def detectCodeLocation = new DetectCodeLocation(BomToolType.CPAN, sourcePath, 'testBdio', 'output', externalId, dependencyGraph)

        bdioCreationUtil.createBdioDocument(new File(outputFilePath), detectCodeLocation)
    }
}
