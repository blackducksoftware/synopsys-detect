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
package com.blackducksoftware.integration.hub.detect.bomtool.conda

import java.lang.reflect.Type

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Component
class CondaListParser {
    @Autowired
    Gson gson

    Set<DependencyNode> parse(String listJsonText) {
        Type listType = new TypeToken<ArrayList<CondaListElement>>(){}.getType()
        List<CondaListElement> condaList = gson.fromJson(listJsonText, listType)

        Set<DependencyNode> dependencies = new HashSet<>()
        condaList.each { dependency ->
            ExternalId externalId = new NameVersionExternalId(Forge.ANACONDA, dependency.name, dependency.version)
            def dependencyNode = new DependencyNode(dependency.name, dependency.version, externalId)
            dependencies.add(dependencyNode)
        }

        dependencies
    }
}
