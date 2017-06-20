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

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer

class DepLockParser {
    private final ProjectInfoGatherer projectInfoGatherer

    public DepLockParser(ProjectInfoGatherer projectInfoGatherer){
        this.projectInfoGatherer = projectInfoGatherer
    }

    public DependencyNode parseDepLock(String depLockContents) {
        return null
    }
}
