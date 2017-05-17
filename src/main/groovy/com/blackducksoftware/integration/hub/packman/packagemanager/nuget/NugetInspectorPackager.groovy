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
        def nugetInspectorExe = new File(getClass().getResource('nuget_inspector.exe').toURI())
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
        node.children.each { dependencyNode.children.add(nugetNodeTransformer(it)) }
        dependencyNode
    }
}
