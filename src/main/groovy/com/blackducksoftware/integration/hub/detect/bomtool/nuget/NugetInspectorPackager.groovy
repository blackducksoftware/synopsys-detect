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
package com.blackducksoftware.integration.hub.detect.bomtool.nuget

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetContainer
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetContainerType
import com.blackducksoftware.integration.hub.detect.bomtool.nuget.model.NugetInspection
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.model.BomToolType
import com.blackducksoftware.integration.hub.detect.model.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.google.gson.Gson
import com.google.gson.stream.JsonReader

@Component
class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class)

    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    DetectFileManager detectFileManager

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    HubSignatureScanner hubSignatureScanner

    @Autowired
    Gson gson

    @Autowired
    NameVersionNodeTransformer nameVersionNodeTransformer


    public List<DetectCodeLocation> createDetectCodeLocation(File dependencyNodeFile) {
        final InputStream inputStream = new FileInputStream(dependencyNodeFile)
        final InputStreamReader streamReader = new InputStreamReader(inputStream, "UTF-8")
        final JsonReader reader = new JsonReader(streamReader)
        final NugetInspection nugetInspection = gson.fromJson(reader, NugetInspection.class)
        def codeLocations = new ArrayList<DetectCodeLocation>();
        nugetInspection.containers.each {
            registerScanPaths(it)
            codeLocations.addAll(createDetectCodeLocationFromNugetContainer(it))
        }

        codeLocations
    }

    private void registerScanPaths(NugetContainer nugetContainer){
        nugetContainer.outputPaths?.each {
            hubSignatureScanner?.registerPathToScan(new File(it))
        }
        nugetContainer.children?.each { registerScanPaths(it) }
    }


    private List<DetectCodeLocation> createDetectCodeLocationFromNugetContainer(NugetContainer nugetContainer) {
        String projectName = ''
        String projectVersionName = ''
        if (NugetContainerType.SOLUTION == nugetContainer.type) {
            projectName = nugetContainer.name
            projectVersionName = nugetContainer.version
            def codeLocations = nugetContainer.children.collect { container ->
                def builder = new NugetDependencyNodeBuilder()
                builder.AddPackageSets(container.packages)
                def children = builder.CreateDependencyNodes(container.dependencies)
                def sourcePath = container.sourcePath

                if (!projectVersionName) {
                    projectVersionName = container.version
                }
                new DetectCodeLocation(BomToolType.NUGET, sourcePath, projectName, projectVersionName, new NameVersionExternalId(Forge.NUGET, projectName, projectVersionName), children)
            }
            return codeLocations
        } else if (NugetContainerType.PROJECT == nugetContainer.type) {
            projectName = nugetContainer.name
            projectVersionName = nugetContainer.version
            String sourcePath = nugetContainer.sourcePath
            def builder = new NugetDependencyNodeBuilder()
            builder.AddPackageSets(nugetContainer.packages)
            def children = builder.CreateDependencyNodes(nugetContainer.dependencies)

            return [
                new DetectCodeLocation(BomToolType.NUGET, sourcePath, projectName, projectVersionName, new NameVersionExternalId(Forge.NUGET, projectName, projectVersionName), children)
            ]
        }
    }
}
