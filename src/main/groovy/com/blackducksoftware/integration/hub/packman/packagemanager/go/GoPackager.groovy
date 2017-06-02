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
package com.blackducksoftware.integration.hub.packman.packagemanager.go

import com.blackducksoftware.integration.hub.bdio.simple.DependencyNodeBuilder
import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.type.PackageManagerType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

class GoPackager {
    private final ProjectInfoGatherer projectInfoGatherer

    public GoPackager(final ProjectInfoGatherer projectInfoGatherer) {
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> makeDependencyNodes(final String sourcePath) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(PackageManagerType.GO, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(Forge.GOGET, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)
        DependencyNodeBuilder nodeBuilder = new DependencyNodeBuilder();
        nodeBuilder.addParentNodeWithChildren(root, null)
        GoDepParser goDepParser = new GoDepParser()
        def goDirectories = findGoDirectories(sourcePath)

        goDirectories.each {
            String goDepContents = getGoDepContents(it)
            def children = goDepParser.parseGoDep(root, goDepContents)
            nodeBuilder.addParentNodeWithChildren(root, children)
        }
        [root]
    }

    private File[] findGoDirectories(String sourcePath){
    }

    private String getGoDepContents(File goDirectory){
        // run godep
        //get Godeps/Godeps.json contents
    }
}
