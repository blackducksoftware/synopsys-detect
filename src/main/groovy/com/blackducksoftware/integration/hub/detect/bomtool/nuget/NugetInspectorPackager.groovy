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

import java.nio.charset.StandardCharsets

import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.bomtool.output.DetectCodeLocation
import com.blackducksoftware.integration.hub.detect.hub.HubSignatureScanner
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeTransformer
import com.blackducksoftware.integration.hub.detect.type.BomToolType
import com.blackducksoftware.integration.hub.detect.util.DetectFileManager
import com.blackducksoftware.integration.hub.detect.util.executable.Executable
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableOutput
import com.blackducksoftware.integration.hub.detect.util.executable.ExecutableRunner
import com.google.gson.Gson

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

    List<DetectCodeLocation> makeDetectCodeLocations(String sourcePath, File nugetExecutable) {
        def outputDirectory = new File(detectConfiguration.outputDirectory, 'nuget')
        def sourceDirectory = new File(sourcePath)
        String inspectorExePath = getInspectorExePath(sourceDirectory, outputDirectory, nugetExecutable)

        if (!inspectorExePath) {
            return null
        }

        def options =  [
            "--target_path=${sourcePath}",
            "--output_directory=${outputDirectory.getAbsolutePath()}",
            "--ignore_failure=${detectConfiguration.getNugetInspectorIgnoreFailure()}"
        ]
        if (detectConfiguration.getNugetInspectorExcludedModules()) {
            options += "--excluded_modules=${detectConfiguration.getNugetInspectorExcludedModules()}"
        }
        if (logger.traceEnabled) {
            options += "-v"
        }

        def hubNugetInspectorExecutable = new Executable(sourceDirectory, inspectorExePath, options)
        ExecutableOutput executableOutput = executableRunner.execute(hubNugetInspectorExecutable)

        def dependencyNodeFiles = detectFileManager.findFiles(outputDirectory, '*_dependency_node.json')
        if (!dependencyNodeFiles) {
            return null
        }
        List<DetectCodeLocation> codeLocations = dependencyNodeFiles.collect { createDetectCodeLocation(it) }
        FileUtils.deleteDirectory(outputDirectory)
        return codeLocations
    }

    private String getInspectorExePath(File sourceDirectory, File outputDirectory, File nugetExecutable) {
        File inspectorVersionDirectory = new File(outputDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.${detectConfiguration.getNugetInspectorPackageVersion()}")
        File toolsDirectory = new File(inspectorVersionDirectory, 'tools')
        File inspectorExe = new File(toolsDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.exe")

        //if we can't find the inspector where we expect to, attempt to install it from nuget.org
        if (inspectorExe == null || !inspectorExe.exists()) {
            installInspectorFromNugetDotOrg(sourceDirectory, outputDirectory, nugetExecutable)
            inspectorExe = new File(toolsDirectory, "${detectConfiguration.getNugetInspectorPackageName()}.exe")
        }

        if (inspectorExe == null || !inspectorExe.exists()) {
            logger.error("Could not find the ${detectConfiguration.getNugetInspectorPackageName()} version:${detectConfiguration.getNugetInspectorPackageVersion()} even after an install attempt.")
            return null
        }

        return inspectorExe.absolutePath
    }

    private ExecutableOutput installInspectorFromNugetDotOrg(File sourceDirectory, File outputDirectory, File nugetExecutable) {
        def options =  [
            'install',
            detectConfiguration.getNugetInspectorPackageName(),
            '-Version',
            detectConfiguration.getNugetInspectorPackageVersion(),
            '-OutputDirectory',
            outputDirectory.absolutePath
        ]

        Executable installExecutable = new Executable(sourceDirectory, nugetExecutable.absolutePath, options)
        executableRunner.execute(installExecutable)
    }

    public List<DetectCodeLocation> createDetectCodeLocation(File dependencyNodeFile) {
        final String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        final NugetNode nugetNode = gson.fromJson(dependencyNodeJson, NugetNode.class)
        registerScanPaths(nugetNode)

        createDetectCodeLocationFromNode(nugetNode)
    }

    private void registerScanPaths(NugetNode nugetNode){
        nugetNode.outputPaths?.each {
            hubSignatureScanner?.registerPathToScan(new File(it))
        }
        nugetNode.children?.each { registerScanPaths(it) }
    }


    private List<DetectCodeLocation> createDetectCodeLocationFromNode(NugetNode nugetNode) {
        String projectName = ''
        String projectVersionName = ''
        // The second part of the if statements are to support < 1.2.0 versions of the Nuget inspector
        if (NodeType.SOLUTION == nugetNode.type || (!nugetNode.type && !nugetNode.version)) {
            projectName = nugetNode.artifact
            // List<DetectCodeLocation> codeLocations = new ArrayList<>()
            // for (NugetNode node : nugetNode.children) {
            return nugetNode.children.collect { node ->
                DependencyNode dependencyNode = nameVersionNodeTransformer.createDependencyNode(Forge.NUGET, node)
                String sourcePath = null
                if (node.sourcePath) {
                    // this field was added to the inspector after 1.1.0
                    sourcePath = node.sourcePath
                } else {
                    sourcePath = node.artifact
                }
                if (!projectVersionName) {
                    projectVersionName = node.version
                }
                def externalId = new NameVersionExternalId(Forge.NUGET, projectName, projectVersionName)
                new DetectCodeLocation(BomToolType.NUGET, sourcePath, projectName, projectVersionName, null, externalId, dependencyNode.children)
            }
        } else if (NodeType.PROJECT == nugetNode.type || (!nugetNode.type && nugetNode.version)) {
            DependencyNode dependencyNode = nameVersionNodeTransformer.createDependencyNode(Forge.NUGET, nugetNode)
            projectName = nugetNode.artifact
            projectVersionName = nugetNode.version
            String sourcePath = ''
            if (nugetNode.sourcePath) {
                // this field was added after 1.1.0
                sourcePath = nugetNode.sourcePath
            } else {
                sourcePath = projectName
            }
            def externalId = new NameVersionExternalId(Forge.NUGET, projectName, projectVersionName)
            return [
                new DetectCodeLocation(BomToolType.NUGET, sourcePath, projectName, projectVersionName, null, externalId, dependencyNode.children)
            ]
        }
    }
}
