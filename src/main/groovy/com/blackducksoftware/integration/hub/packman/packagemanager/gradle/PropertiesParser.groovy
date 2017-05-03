package com.blackducksoftware.integration.hub.packman.packagemanager.gradle

import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId

@Component
class PropertiesParser {
    DependencyNode createProjectDependencyNodeFromProperties(String properties) {
        String group = 'unknown'
        String name = 'unknown'
        String version = 'unknown'
        boolean processingProperties = false
        properties.split('\n').each { line ->
            if (line.startsWith(':properties')) {
                processingProperties = true
            }
            if (processingProperties) {
                if ('unknown' == group && line.startsWith('group: ') && line.length() > 7) {
                    group = line[7..-1]
                } else if ('unknown' == name && line.startsWith('name: ') && line.length() > 6) {
                    name = line[6..-1]
                } else if ('unknown' == version && line.startsWith('version: ') && line.length() > 9) {
                    version = line[9..-1]
                }
            }
        }

        new DependencyNode(name, version, new MavenExternalId(group, name, version))
    }
}
