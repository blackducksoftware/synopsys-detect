package com.blackducksoftware.integration.hub.packman.bomtool.docker

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.packman.PackmanProperties

@Component
class DockerProperties {
    @Autowired
    PackmanProperties packmanProperties

    private static final String HUB_URL = "hub.url"
    private static final String HUB_TIMEOUT = "hub.timeout"
    private static final String HUB_USERNAME = "hub.username"
    private static final String HUB_PASSWORD = "hub.password"
    private static final String HUB_PROXY_HOST = "hub.proxy.host"
    private static final String HUB_PROXY_PORT = "hub.proxy.port"
    private static final String HUB_PROXY_USERNAME = "hub.proxy.username"
    private static final String HUB_PROXY_PASSWORD = "hub.proxy.password"
    private static final String HUB_PROJECT_NAME = "hub.project.name"
    private static final String HUB_PROJECT_VERSION = "hub.project.version"
    private static final String DOCKER_TAR = "docker.tar"
    private static final String DOCKER_IMAGE = "docker.image"
    private static final String INSTALL_DIR = "install.dir"
    private static final String WORKING_DIRECTORY = "working.directory"
    private static final String LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE = "logging.level.com.blackducksoftware"

    @PostConstruct
    void init() {
        List<String> args = createDockerArgumentList()
        args.each { println it }
    }

    List<String> createDockerArgumentList() {
        def arguments = []
        constructArgument(arguments, 'packman.hub.url', HUB_URL)
        constructArgument(arguments, 'packman.hub.timeout', HUB_TIMEOUT)
        constructArgument(arguments, 'packman.hub.username', HUB_USERNAME)
        constructArgument(arguments, 'packman.hub.password', HUB_PASSWORD)
        constructArgument(arguments, 'packman.hub.proxy.host', HUB_PROXY_HOST)
        constructArgument(arguments, 'packman.hub.proxy.port', HUB_PROXY_PORT)
        constructArgument(arguments, 'packman.hub.proxy.username', HUB_PROXY_USERNAME)
        constructArgument(arguments, 'packman.hub.proxy.password', HUB_PROXY_PASSWORD)
        constructArgument(arguments, 'packman.project.name', HUB_PROJECT_NAME)
        constructArgument(arguments, 'packman.project.version', HUB_PROJECT_VERSION)
        constructArgument(arguments, 'packman.docker.tar', DOCKER_TAR)
        constructArgument(arguments, 'packman.docker.image', DOCKER_IMAGE)
        constructArgument(arguments, 'packman.docker.install.path', INSTALL_DIR)
        constructArgument(arguments, 'packman.docker.sandbox.path', WORKING_DIRECTORY)
        constructArgument(arguments, 'logging.level.com.blackducksoftware.integration', LOGGING_LEVEL_COM_BLACKDUCKSOFTWARE)

        for (String propertyName : packmanProperties.getAdditionalDockerPropertyNames()) {
            String dockerKey = propertyName[PackmanProperties.DOCKER_PROPERTY_PREFIX.length()..-1]
            constructArgument(arguments, propertyName, dockerKey)
        }

        arguments
    }

    private String constructArgument(List<String> arguments, String key, String dockerKey) {
        String value = packmanProperties.getProperty(key)
        if (value) {
            arguments.add("--${dockerKey}=${value}")
        }
    }
}