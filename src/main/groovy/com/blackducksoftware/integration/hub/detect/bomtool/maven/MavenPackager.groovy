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

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner

@Component
public class MavenPackager {
    private final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    DetectConfiguration detectConfiguration

    public List<DependencyNode> makeDependencyNodes(String sourcePath, String mavenExecutable) {
        final List<DependencyNode> projects = []

        File sourceDirectory = new File(sourcePath)

        def arguments = ["dependency:tree"]
        if (detectConfiguration.getMavenScope()?.trim()) {
            arguments.add("-Dscope=${detectConfiguration.getMavenScope()}")
        }
        final Executable mvnExecutable = new Executable(sourceDirectory, mavenExecutable, arguments)
        final ExecutableOutput mvnOutput = executableRunner.execute(mvnExecutable)

        final MavenOutputParser mavenOutputParser = new MavenOutputParser()
        projects.addAll(mavenOutputParser.parse(mvnOutput.standardOutput))

        if (detectConfiguration.getMavenAggregateBom() && !projects.isEmpty()) {
            final DependencyNode firstNode = projects.remove(0)
            projects.each { subProject ->
                firstNode.children.addAll(subProject.children)
            }
            projects.clear()
            projects.add(firstNode)
            firstNode.name = projectInfoGatherer.getDefaultProjectName(BomToolType.MAVEN, sourcePath, firstNode.name)
            firstNode.version = projectInfoGatherer.getDefaultProjectVersionName(firstNode.version)
        }

        return projects
    }
}