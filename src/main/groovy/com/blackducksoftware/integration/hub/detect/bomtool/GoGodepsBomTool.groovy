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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.bomtool.go.GoGodepsParser
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.google.gson.Gson

@Component
class GoGodepsBomTool extends BomTool {
    private final Logger logger = LoggerFactory.getLogger(GoGodepsBomTool.class)

    @Autowired
    Gson gson

    List<String> matchingSourcePaths = []

    @Override
    public BomToolType getBomToolType() {
        return BomToolType.GO_GODEP
    }

    @Override
    public boolean isBomToolApplicable() {
        matchingSourcePaths = sourcePathSearcher.findFilenamePattern('Godeps')
        !matchingSourcePaths.isEmpty()
    }

    @Override
    public List<DependencyNode> extractDependencyNodes() {
        def nodes = []
        GoGodepsParser goDepParser = new GoGodepsParser(gson, projectInfoGatherer)
        matchingSourcePaths.each {
            def goDepsDirectory = new File(it, "Godeps")
            def goDepsFile = new File(goDepsDirectory, "Godeps.json")
            if (goDepsFile.exists()) {
                def dependencyNode = goDepParser.parseGoDep(goDepsFile.text)
                nodes.addAll(dependencyNode)
            }
        }
        return nodes
    }
}
