package com.blackducksoftware.integration.hub.packman;

import javax.annotation.PostConstruct

import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component

@Component
class PackmanProperties {
    @Value('${packman.source.paths}')
    String[] sourcePaths

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
