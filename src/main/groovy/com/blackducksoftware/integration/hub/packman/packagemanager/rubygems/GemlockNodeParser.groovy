package com.blackducksoftware.integration.hub.packman.packagemanager.rubygems

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.util.NameVersionNode
import com.blackducksoftware.integration.hub.packman.util.NameVersionNodeBuilder

class GemlockNodeParser {
    private final Logger logger = LoggerFactory.getLogger(GemlockNodeParser.class)

    private NameVersionNode rootNameVersionNode
    private NameVersionNodeBuilder nameVersionNodeBuilder
    private HashSet<String> directDependencyNames
    private NameVersionNode currentParent

    private boolean inGemSection = false
    private boolean inSpecsSection = false
    private boolean inDependenciesSection = false

    void parseProjectDependencies(DependencyNode rootProject, final String gemfileLockContents) {
        rootNameVersionNode = new NameVersionNode([name: rootProject.name, version: rootProject.version])
        nameVersionNodeBuilder = new NameVersionNodeBuilder(rootNameVersionNode)
        directDependencyNames = new HashSet<>()
        currentParent = null

        String[] lines = gemfileLockContents.split('\n')
        for (String line : lines) {
            if (!inGemSection) {
                if ('GEM' == line) {
                    inGemSection = true
                }
                continue
            }

            if (!line?.trim()) {
                inSpecsSection = false
                inDependenciesSection = false
                continue
            }

            if (!inSpecsSection && '  specs:' == line) {
                inSpecsSection = true
                continue
            }

            if (!inDependenciesSection && 'DEPENDENCIES' == line) {
                inDependenciesSection = true
                continue
            }

            if (!inSpecsSection && !inDependenciesSection) {
                continue
            }

            //we are now either in the specs section or in the dependencies section
            if (inSpecsSection) {
                parseSpecsSectionLine(line)
            } else {
                parseDependencySectionLine(line)
            }
        }

        directDependencyNames.each { directDependencyName ->
            NameVersionNode nameVersionNode = nameVersionNodeBuilder.nameToNodeMap[directDependencyName]
            if (nameVersionNode) {
                DependencyNode directDependencyNode = createDependencyNode(nameVersionNode)
                rootProject.children.add(directDependencyNode)
            } else {
                logger.error("Could not find ${directDependencyName} in the populated map.")
            }
        }
    }

    private void parseSpecsSectionLine(String line) {
        if (line.startsWith('      ')) {
            if (!currentParent) {
                logger.error("Trying to add a child without a parent: ${line}")
            } else {
                NameVersionNode childNode = createNameVersionNode(line)
                nameVersionNodeBuilder.addChildNodeToParent(childNode, currentParent)
            }
        } else if (line.startsWith('    ')) {
            currentParent = createNameVersionNode(line)
            nameVersionNodeBuilder.addChildNodeToParent(currentParent, rootNameVersionNode)
        } else {
            logger.error("Line in specs section can't be parsed: ${line}")
        }
    }

    private void parseDependencySectionLine(String line) {
        NameVersionNode dependencyNameVersionNode = createNameVersionNode(line)
        if (!dependencyNameVersionNode.name) {
            logger.error("Line in dependencies section can't be parsed: ${line}")
        } else {
            directDependencyNames.add(dependencyNameVersionNode.name)
        }
    }

    private NameVersionNode createNameVersionNode(String line) {
        def name = line.trim()
        def version = ''
        int spaceIndex = name.indexOf(' ')
        if (spaceIndex > 0) {
            version = parseValidVersion(name[spaceIndex..-1].trim())
            name = name[0..spaceIndex].trim()
        }

        new NameVersionNode([name: name, version: version])
    }

    //a valid version looks like (###.###.###)
    private String parseValidVersion(String version) {
        if (version[0] != '(' || version[-1] != ')' || version.indexOf('~>') >= 0 || version.indexOf('>=') >= 0) {
            return ''
        } else {
            return version[1..-2]
        }
    }

    private DependencyNode createDependencyNode(NameVersionNode nameVersionNode) {
        def name = nameVersionNode.name
        def version = nameVersionNode.version
        def children = new HashSet<>()

        DependencyNode dependencyNode = new DependencyNode(name, version, new NameVersionExternalId(Forge.RUBYGEMS, name, version), children)
        if (nameVersionNode.children) {
            nameVersionNode.children.each {
                children.add(createDependencyNode(it))
            }
        }

        dependencyNode
    }
}