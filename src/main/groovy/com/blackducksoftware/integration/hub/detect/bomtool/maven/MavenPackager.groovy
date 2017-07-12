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
package com.blackducksoftware.integration.hub.detect.bomtool.maven

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectConfiguration

@Component
public class MavenPackager {
    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    MavenOutputParser mavenOutputParser

    public List<DependencyNode> makeDependencyNodes(String mavenOutput) {
        List<DependencyNode> projects = mavenOutputParser.parse(mavenOutput)

        if (detectConfiguration.getMavenAggregateBom() && !projects.isEmpty()) {
            final DependencyNode rootNode = projects.remove(0)
            projects.each { subProject ->
                rootNode.children.addAll(subProject.children)
            }
            return [rootNode]
        }

        projects
    }
}