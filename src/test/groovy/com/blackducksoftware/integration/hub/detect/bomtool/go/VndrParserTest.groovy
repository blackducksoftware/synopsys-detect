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
package com.blackducksoftware.integration.hub.detect.bomtool.go

import com.blackducksoftware.integration.hub.bdio.graph.DependencyGraph
import com.blackducksoftware.integration.hub.bdio.model.externalid.ExternalIdFactory
import com.blackducksoftware.integration.hub.detect.extraction.bomtool.go.parse.VndrParser
import com.blackducksoftware.integration.hub.detect.testutils.DependencyGraphResourceTestUtil
import com.blackducksoftware.integration.hub.detect.testutils.TestUtil
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.junit.Assert
import org.junit.Test

class VndrParserTest {
    Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()

    @Test
    public void vndrParserTest() throws IOException {
        def testUtil = new TestUtil()
        final VndrParser vndrParser = new VndrParser()
        vndrParser.externalIdFactory = new ExternalIdFactory()

        def text = testUtil.getResourceAsUTF8String('/go/vendor.conf')
        final List<String> vendorConfContents = text.split(System.lineSeparator).toList()
        final DependencyGraph dependencyGraph = vndrParser.parseVendorConf(vendorConfContents)

        Assert.assertNotNull(dependencyGraph)
        DependencyGraphResourceTestUtil.assertGraph('/go/Go_VndrExpected_graph.json', dependencyGraph)
    }
}
