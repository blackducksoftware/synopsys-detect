package com.blackducksoftware.integration.hub.packman

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.buildtool.BuildToolConstants
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class)

    @Value('${packman.output.path}')
    String outputDirectoryPath

    @Value('${packman.cleanup.bdio.files}')
    String cleanupBdioFiles

    @Value('${packman.hub.url}')
    String hubUrl

    @Value('${packman.hub.username}')
    String hubUsername

    @Value('${packman.hub.password}')
    String hubPassword

    void uploadBdioFiles(List<File> createdBdioFiles) {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.hubUrl = hubUrl
        hubServerConfigBuilder.username = hubUsername
        hubServerConfigBuilder.password = hubPassword

        HubServerConfig hubServerConfig = hubServerConfigBuilder.build()
        Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
        RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)

        HubServicesFactory hubServicesFactory = new HubServicesFactory(restConnection);

        BomImportRequestService bomImportRequestService = hubServicesFactory.createBomImportRequestService()

        createdBdioFiles.each { file ->
            logger.info("uploading ${file.name} to ${hubUrl}")
            bomImportRequestService.importBomFile(file, BuildToolConstants.BDIO_FILE_MEDIA_TYPE)
            if (Boolean.valueOf(cleanupBdioFiles)) {
                file.delete()
            }
        }
    }
}
