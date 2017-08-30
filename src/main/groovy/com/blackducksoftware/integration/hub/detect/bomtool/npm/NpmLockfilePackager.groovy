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
package com.blackducksoftware.integration.hub.detect.bomtool.npm

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeBuilder
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.nameversion.builder.LinkedNameVersionNodeBuilder
import com.google.gson.Gson

import groovy.transform.TypeChecked

@Component
@TypeChecked
class NpmLockfilePackager {
    @Autowired
    Gson gson

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public DependencyNode parse(String lockFileText) {
        NpmProject npmProject = gson.fromJson(lockFileText, NpmProject.class)

        NameVersionNode root = new NameVersionNodeImpl([name: npmProject.name, version: npmProject.version])
        NameVersionNodeBuilder builder = new LinkedNameVersionNodeBuilder(root)

        npmProject.dependencies.each { name, npmDependency ->
            NameVersionNode dependency = new NameVersionNodeImpl([name: name, version: npmDependency.version])
            builder.addChildNodeToParent(dependency, root)

            npmDependency.requires?.each { childName, childVersion ->
                NameVersionNode child = new NameVersionNodeImpl([name: childName, version: childVersion])
                builder.addChildNodeToParent(child, dependency)
            }
        }

        nameVersionNodeTransformer.createDependencyNode(Forge.NPM, builder.build())
    }
}
