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
package com.blackducksoftware.integration.hub.packman.packagemanager.nuget

import java.nio.charset.StandardCharsets

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.ExternalId
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.google.gson.Gson

@Component
class NugetInspectorPackager {
    private final Logger logger = LoggerFactory.getLogger(NugetInspectorPackager.class)

    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    Gson gson

    @Autowired
    FileFinder fileFinder

    DependencyNode makeDependencyNode(String sourcePath) {
        def outputDirectory = new File(packmanProperties.outputDirectoryPath)
        def dependencyNodeFile = new File(outputDirectory, 'dependencyNodes.json')
        def nugetInspectorExe = new File(getClass().getResource('nuget/HubNugetInspecter.exe').toURI())

        File hubInspectorNupkg = fetchFromNuget(outputDirectory, 'HubNugetInspector')
        if(hubInspectorNupkg) {
            // TODO: We need to extract the executable from the nupkg file
            // nugetInspectorExe = new File()
        }

        String command = "${nugetInspectorExe.getAbsolutePath()} --target_path=${sourcePath} --output_directory=${outputDirectory.getAbsolutePath()}"
        command.execute()
        String dependencyNodeJson = dependencyNodeFile.getText(StandardCharsets.UTF_8.name())
        NugetNode solution = gson.fromJson(dependencyNodeJson, NugetNode.class)

        dependencyNodeFile.delete()
        nugetNodeTransformer(solution)
    }

    DependencyNode nugetNodeTransformer(NugetNode node) {
        String name = node.artifact
        String version = node.version
        ExternalId externalId = new NameVersionExternalId(Forge.nuget, name, version)
        DependencyNode dependencyNode = new DependencyNode(name, version, externalId)
        node.children.each {
            dependencyNode.children.add(nugetNodeTransformer(it))
        }
        dependencyNode
    }

    File fetchFromNuget(File outputDirectory, String nugetPackageName) {
        def outputFile = new File(outputDirectory, "${nugetPackageName}.nupkg")
        URL v1 = new URL("https://www.nuget.org/api/v1/package/${nugetPackageName}")
        URL v2 = new URL("https://www.nuget.org/api/v2/package/${nugetPackageName}")
        File downloadedFile
        try{
            downloadedFile = downloadFromUrl(outputFile, v2)
        }catch (IOException e) {
            logger.info("Failed to download package from ${v2.toString()}")
        }
        if(!downloadedFile) {
            try{
                downloadedFile = downloadFromUrl(outputFile, v1)
            }catch (IOException e) {
                logger.info("Failed to download package from ${v1.toString()}")
            }
        }
        downloadedFile
    }


    void downloadFromUrl(File outputFile, URL url) throws IOException {
        logger.info("Attempting to download file from ${url.toString()}")
        InputStream fileStream = url.openStream()
        def outputStream = new FileOutputStream(outputFile)
        outputStream.write(fileStream.getBytes())
        outputStream.close()
        logger.info("Succesfully downloaded file from ${url.toString()} to ${outputFile.getAbsolutePath()}")
    }
}
