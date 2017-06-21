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
package com.blackducksoftware.integration.hub.detect.bomtool

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.go.vndr.VndrParser
import com.blackducksoftware.integration.hub.detect.type.BomToolType

@Component
class GoVndrBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GoVndrBomTool.class)

    List<String> matchingSourcePaths = []

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_VNDR
    }

    @Override
    public boolean isBomToolApplicable() {
        matchingSourcePaths = sourcePathSearcher.findFilenamePattern('vendor.conf')
        !matchingSourcePaths.isEmpty()
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        def nodes = []
        VndrParser vndrParser = new VndrParser(projectInfoGatherer)
        matchingSourcePaths.each {
            def vendorConf = new File(it, "vendor.conf")
            if (vendorConf.exists()) {
                def children = vndrParser.parseVendorConf(vendorConf.text)
                nodes.addAll(children)
            }
        }
        return nodes
    }
}
