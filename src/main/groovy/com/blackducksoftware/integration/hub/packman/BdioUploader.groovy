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

    @Value('${packman.cleanup.bdio.files}')
    String cleanupBdioFiles

    @Value('${packman.hub.url}')
    String hubUrl

    @Value('${packman.hub.timeout}')
    String hubTimeout

    @Value('${packman.hub.username}')
    String hubUsername

    @Value('${packman.hub.password}')
    String hubPassword

    @Value('${packman.hub.proxy.host}')
    String hubProxyHost

    @Value('${packman.hub.proxy.port}')
    String hubProxyPort

    @Value('${packman.hub.proxy.username}')
    String hubProxyUsername

    @Value('${packman.hub.proxy.password}')
    String hubProxyPassword

    void uploadBdioFiles(List<File> createdBdioFiles) {
        if (!createdBdioFiles) {
            return
        }

        try {
            HubServerConfig hubServerConfig = createBuilder().build()
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
        } catch (Exception e) {
            logger.error("Your Hub configuration is not valid: ${e.message}")
        }
    }

    private HubServerConfigBuilder createBuilder() {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.setHubUrl(hubUrl)
        hubServerConfigBuilder.setTimeout(hubTimeout)
        hubServerConfigBuilder.setUsername(hubUsername)
        hubServerConfigBuilder.setPassword(hubPassword)

        hubServerConfigBuilder.setProxyHost(hubProxyHost)
        hubServerConfigBuilder.setProxyPort(hubProxyPort)
        hubServerConfigBuilder.setProxyUsername(hubProxyUsername)
        hubServerConfigBuilder.setProxyPassword(hubProxyPassword)

        hubServerConfigBuilder
    }
}
