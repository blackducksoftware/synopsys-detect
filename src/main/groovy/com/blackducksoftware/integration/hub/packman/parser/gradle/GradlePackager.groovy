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
package com.blackducksoftware.integration.hub.packman.parser.gradle

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId
import com.blackducksoftware.integration.hub.packman.parser.model.Packager

class GradlePackager extends Packager {
    GradlePackager(final String pathContainingBuildGradle) {
    }

    @Override
    List<DependencyNode> makeDependencyNodes() {
        def packages = []

        packages.add(new DependencyNode('testName', 'testVersion', new MavenExternalId(Forge.maven, 'testGroup', 'testName', 'testVersion')))

        return packages
    }

    List<DependencyNode> createDependencyNodesFromGradleOutput(String output) {
    }
}