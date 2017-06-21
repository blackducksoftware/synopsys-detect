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
package com.blackducksoftware.integration.hub.detect.bomtool.go.vndr

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.bomtool.GoDepBomTool
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer

class VndrParser {
    private final ProjectInfoGatherer projectInfoGatherer

    public VndrParser(ProjectInfoGatherer projectInfoGatherer){
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> parseVendorConf(String vendorConfContents) {
        List<DependencyNode> nodes = new ArrayList<>()
        String contents = vendorConfContents.trim()
        def lines = contents.split(System.lineSeparator())
        //TODO test against moby
        lines.each { line ->
            if (line?.trim() && !line.startsWith('#')){
                def parts = line.split(' ')
                final ExternalId dependencyExternalId = new NameVersionExternalId(GoDepBomTool.GOLANG, parts[0], parts[1])
                final DependencyNode dependency = new DependencyNode(parts[0], parts[1], dependencyExternalId)
                nodes.add(dependency)
            }
        }

        return nodes
    }
}
