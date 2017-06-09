package com.blackducksoftware.integration.hub.detect.bomtool.docker

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.detect.DetectProperties

@Component
class DockerProperties {
    @Autowired
    DetectConfiguration detectConfiguration

    @Autowired
    DetectProperties detectProperties

    List<String> createDockerArgumentList() {
        def arguments = []
        constructArgument(arguments, 'detect.hub.url', 'hub.url')
        constructArgument(arguments, 'detect.hub.timeout', 'hub.timeout')
        constructArgument(arguments, 'detect.hub.username', 'hub.username')
        constructArgument(arguments, 'detect.hub.password', 'hub.password')
        constructArgument(arguments, 'detect.hub.proxy.host', 'hub.proxy.host')
        constructArgument(arguments, 'detect.hub.proxy.port', 'hub.proxy.port')
        constructArgument(arguments, 'detect.hub.proxy.username', 'hub.proxy.username')
        constructArgument(arguments, 'detect.hub.proxy.password', 'hub.proxy.password')
        constructArgument(arguments, 'detect.project.name', 'hub.project.name')
        constructArgument(arguments, 'detect.project.version.name', 'hub.project.version')
        constructArgument(arguments, 'detect.docker.install.path', 'install.dir')
        constructArgument(arguments, 'detect.docker.sandbox.path', 'working.directory')
        constructArgument(arguments, 'logging.level.com.blackducksoftware.integration', 'logging.level.com.blackducksoftware')

        detectConfiguration.additionalDockerPropertyNames.each { propertyName ->
            String dockerKey = propertyName[DetectConfiguration.DOCKER_PROPERTY_PREFIX.length()..-1]
            constructArgument(arguments, propertyName, dockerKey)
        }

        if (detectProperties.dockerImage) {
            arguments.add(detectProperties.dockerImage)
        } else {
            arguments.add(detectProperties.dockerTar)
        }

        arguments
    }

    private String constructArgument(List<String> arguments, String key, String dockerKey) {
        String value = detectConfiguration.getDetectProperty(key)
        if (value) {
            arguments.add("--${dockerKey}=${value}")
        }
    }
}