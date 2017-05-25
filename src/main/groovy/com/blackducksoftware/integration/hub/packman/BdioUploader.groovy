package com.blackducksoftware.integration.hub.packman

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.buildtool.BuildToolConstants
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.packman.help.ValueDescription
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class)

    @ValueDescription(key="packman.cleanup.bdio.files", description="If true the bdio files will be deleted after upload")
    @Value('${packman.cleanup.bdio.files}')
    String cleanupBdioFiles

    @ValueDescription(key="packman.hub.url", description="URL of the Hub server")
    @Value('${packman.hub.url}')
    String hubUrl

    @ValueDescription(key="packman.hub.timeout", description="Time to wait for rest connections to complete")
    @Value('${packman.hub.timeout}')
    String hubTimeout

    @ValueDescription(key="packman.hub.username", description="Hub username")
    @Value('${packman.hub.username}')
    String hubUsername

    @ValueDescription(key="packman.hub.password", description="Hub password")
    @Value('${packman.hub.password}')
    String hubPassword

    @ValueDescription(key="packman.hub.proxy.host", description="Proxy host")
    @Value('${packman.hub.proxy.host}')
    String hubProxyHost

    @ValueDescription(key="packman.hub.proxy.port", description="Proxy port")
    @Value('${packman.hub.proxy.port}')
    String hubProxyPort

    @ValueDescription(key="packman.hub.proxy.username", description="Proxy username")
    @Value('${packman.hub.proxy.username}')
    String hubProxyUsername

    @ValueDescription(key="packman.hub.proxy.password", description="Proxy password")
    @Value('${packman.hub.proxy.password}')
    String hubProxyPassword

    @ValueDescription(key="packman.hub.auto.import.cert", description="If true the Hub https certificate will be automatically imported")
    @Value('${packman.hub.auto.import.cert}')
    String hubAutoImportCertificate


    void uploadBdioFiles(List<File> createdBdioFiles) {
        if (!createdBdioFiles) {
            return
        }

        try {
            Slf4jIntLogger slf4jIntLogger = new Slf4jIntLogger(logger)
            HubServerConfig hubServerConfig = createBuilder(slf4jIntLogger).build()
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

    private HubServerConfigBuilder createBuilder(Slf4jIntLogger slf4jIntLogger) {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.setHubUrl(hubUrl)
        hubServerConfigBuilder.setTimeout(hubTimeout)
        hubServerConfigBuilder.setUsername(hubUsername)
        hubServerConfigBuilder.setPassword(hubPassword)

        hubServerConfigBuilder.setProxyHost(hubProxyHost)
        hubServerConfigBuilder.setProxyPort(hubProxyPort)
        hubServerConfigBuilder.setProxyUsername(hubProxyUsername)
        hubServerConfigBuilder.setProxyPassword(hubProxyPassword)

        hubServerConfigBuilder.setAutoImportHttpsCertificates(Boolean.valueOf(hubAutoImportCertificate))
        hubServerConfigBuilder.setLogger(slf4jIntLogger)

        hubServerConfigBuilder
    }
}
