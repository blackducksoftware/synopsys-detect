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
package com.blackducksoftware.integration.hub.packman.bomtool.rubygems

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer

public class RubygemsNodePackager {
    private final ProjectInfoGatherer projectInfoGatherer

    public RubygemsNodePackager(final ProjectInfoGatherer projectInfoGatherer) {
        this.projectInfoGatherer = projectInfoGatherer
    }

    public List<DependencyNode> makeDependencyNodes(final String sourcePath, final String gemlock) {
        final String rootName = projectInfoGatherer.getDefaultProjectName(BomToolType.RUBYGEMS, sourcePath)
        final String rootVersion = projectInfoGatherer.getDefaultProjectVersionName()
        final ExternalId rootExternalId = new NameVersionExternalId(Forge.RUBYGEMS, rootName, rootVersion)
        final DependencyNode root = new DependencyNode(rootName, rootVersion, rootExternalId)

        GemlockNodeParser gemlockNodeParser = new GemlockNodeParser()
        gemlockNodeParser.parseProjectDependencies(root, gemlock)

        [root]
    }
}
