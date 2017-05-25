package com.blackducksoftware.integration.hub.packman

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.packman.help.ValueDescription

@Component
class PackmanProperties {
    @ValueDescription(key = "packman.source.paths", description = "Source paths to inspect")
    @Value('${packman.source.paths}')
    String[] sourcePaths

    @ValueDescription(key = "packman.output.path", description = "Output path")
    @Value('${packman.output.path}')
    String outputDirectoryPath

    @PostConstruct
    void init() {
        if (sourcePaths == null || sourcePaths.length == 0) {
            sourcePaths = [
                System.getProperty('user.dir')
            ] as String[]
        }

        if (StringUtils.isBlank(outputDirectoryPath)) {
            outputDirectoryPath = System.getProperty('user.home') + File.separator + 'blackduck'
        }

        new File(outputDirectoryPath).mkdirs()
    }
}
