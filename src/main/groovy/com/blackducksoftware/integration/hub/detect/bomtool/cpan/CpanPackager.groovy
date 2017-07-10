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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
class CpanPackager {
    private final Logger logger = LoggerFactory.getLogger(CpanPackager.class)

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    CpanListParser cpanListParser

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer

    public Set<DependencyNode> makeDependencyNodes(File sourceDirectory, String cpanListText, String directDependenciesText) {
        Map<String, NameVersionNode> allModules = cpanListParser.parse(cpanListText)
        List<String> directModuleNames = getDirectModuleNames(directDependenciesText)

        Set<DependencyNode> dependencyNodes = []
        directModuleNames.each { moduleName ->
            def nameVersionNode = allModules[moduleName]
            if (nameVersionNode) {
                DependencyNode module = nameVersionNodeTransformer.createDependencyNode(Forge.CPAN, nameVersionNode)
                dependencyNodes += module
            } else {
                logger.warn("Could node find resolved version for module: ${moduleName}")
            }
        }

        dependencyNodes
    }

    private List<String> getDirectModuleNames(String directDependenciesText) {
        List<String> modules = []
        for (String line : directDependenciesText.split('\n')) {
            if (!line?.trim()) {
                continue
            }
            if (line.contains('-->') || (line.contains(' ... ') && line.contains('Configuring'))) {
                continue
            }
            modules += line.split('~')[0].trim()
        }

        modules
    }
}
