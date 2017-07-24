/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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

    Set<DependencyNode> parse(String listJsonText, String infoJsonText) {
        Type listType = new TypeToken<ArrayList<CondaListElement>>(){}.getType()
        List<CondaListElement> condaList = gson.fromJson(listJsonText, listType)

        CondaInfo condaInfo = gson.fromJson(infoJsonText, CondaInfo.class)

        Set<DependencyNode> dependencies = new HashSet<>()
        condaList.each { dependency ->
            String name = dependency.name
            String version = "${dependency.version}-${dependency.buildString}-${condaInfo.platform}"
            ExternalId externalId = new NameVersionExternalId(Forge.ANACONDA, name, version)
            def dependencyNode = new DependencyNode(dependency.name, dependency.version, externalId)
            dependencies.add(dependencyNode)
        }

        dependencies
    }
}
