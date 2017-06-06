package com.blackducksoftware.integration.hub.packman.bomtool.docker

import org.springframework.stereotype.Component

@Component
class DockerProperties {


    String hubUrl = "hub.url"
    String hubTimeout = "hub.timeout"
    String hubUsername = "hub.username"
    String hubPassword = "hub.password"
    String hubProxyHost = "hub.proxy.host"
    String hubProxyPort = "hub.proxy.port"
    String hubProxyUsername = "hub.proxy.username"
    String hubProxyPassword = "hub.proxy.password"
    String hubProjectName = "hub.project.name"
    String hubProjectVersion = "hub.project.version"
    String workingDirectory = "working.directory"
    String dockerTar = "docker.tar"
    String dockerImage = "docker.image"
    String installDir = "install.dir"
    String loggingLevelComBlackducksoftware = "logging.level.com.blackducksoftware"
}
