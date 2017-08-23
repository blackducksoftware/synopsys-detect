package com.blackducksoftware.integration.hub.detect.hub

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.api.bom.BomImportRequestService
import com.blackducksoftware.integration.hub.api.codelocation.CodeLocationRequestService
import com.blackducksoftware.integration.hub.api.item.MetaService
import com.blackducksoftware.integration.hub.api.project.ProjectRequestService
import com.blackducksoftware.integration.hub.api.project.version.ProjectVersionRequestService
import com.blackducksoftware.integration.hub.api.scan.ScanSummaryRequestService
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder
import com.blackducksoftware.integration.hub.dataservice.cli.CLIDataService
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService
import com.blackducksoftware.integration.hub.dataservice.policystatus.PolicyStatusDataService
import com.blackducksoftware.integration.hub.dataservice.project.ProjectDataService
import com.blackducksoftware.integration.hub.dataservice.report.RiskReportDataService
import com.blackducksoftware.integration.hub.dataservice.scan.ScanStatusDataService
import com.blackducksoftware.integration.hub.detect.DetectConfiguration
import com.blackducksoftware.integration.hub.global.HubServerConfig
import com.blackducksoftware.integration.hub.rest.RestConnection
import com.blackducksoftware.integration.hub.service.HubServicesFactory
import com.blackducksoftware.integration.log.IntLogger
import com.blackducksoftware.integration.log.Slf4jIntLogger

@Component
class HubServiceWrapper {
    private final Logger logger = LoggerFactory.getLogger(HubServiceWrapper.class)

    @Autowired
    DetectConfiguration detectConfiguration

    IntLogger slf4jIntLogger
    HubServerConfig hubServerConfig
    HubServicesFactory hubServicesFactory

    void init(Logger logger) {
        if (hubServicesFactory == null) {
            slf4jIntLogger = new Slf4jIntLogger(logger)
            hubServerConfig = createHubServerConfig(slf4jIntLogger)
            hubServicesFactory = createHubServicesFactory(slf4jIntLogger, hubServerConfig)
        }
    }

    ProjectRequestService createProjectRequestService() {
        hubServicesFactory.createProjectRequestService(slf4jIntLogger)
    }

    ProjectVersionRequestService createProjectVersionRequestService() {
        hubServicesFactory.createProjectVersionRequestService(slf4jIntLogger)
    }

    BomImportRequestService createBomImportRequestService() {
        hubServicesFactory.createBomImportRequestService()
    }

    PhoneHomeDataService createPhoneHomeDataService() {
        hubServicesFactory.createPhoneHomeDataService(slf4jIntLogger)
    }

    ProjectDataService createProjectDataService() {
        hubServicesFactory.createProjectDataService(slf4jIntLogger)
    }

    CodeLocationRequestService createCodeLocationRequestService() {
        hubServicesFactory.createCodeLocationRequestService(slf4jIntLogger)
    }

    MetaService createMetaService() {
        hubServicesFactory.createMetaService(slf4jIntLogger)
    }

    ScanSummaryRequestService createScanSummaryRequestService() {
        hubServicesFactory.createScanSummaryRequestService()
    }

    ScanStatusDataService createScanStatusDataService() {
        hubServicesFactory.createScanStatusDataService(slf4jIntLogger, detectConfiguration.getPolicyCheckTimeout())
    }

    PolicyStatusDataService createPolicyStatusDataService() {
        hubServicesFactory.createPolicyStatusDataService(slf4jIntLogger)
    }

    RiskReportDataService createRiskReportDataService() {
        hubServicesFactory.createRiskReportDataService(slf4jIntLogger, 30000)
    }

    CLIDataService createCliDataService() {
        hubServicesFactory.createCLIDataService(slf4jIntLogger, 120000L)
    }

    private HubServicesFactory createHubServicesFactory(Slf4jIntLogger slf4jIntLogger, HubServerConfig hubServerConfig) {
        RestConnection restConnection = hubServerConfig.createCredentialsRestConnection(slf4jIntLogger)

        new HubServicesFactory(restConnection)
    }

    private HubServerConfig createHubServerConfig(Slf4jIntLogger slf4jIntLogger) {
        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder()
        hubServerConfigBuilder.setHubUrl(detectConfiguration.getHubUrl())
        hubServerConfigBuilder.setTimeout(detectConfiguration.getHubTimeout())
        hubServerConfigBuilder.setUsername(detectConfiguration.getHubUsername())
        hubServerConfigBuilder.setPassword(detectConfiguration.getHubPassword())

        hubServerConfigBuilder.setProxyHost(detectConfiguration.getHubProxyHost())
        hubServerConfigBuilder.setProxyPort(detectConfiguration.getHubProxyPort())
        hubServerConfigBuilder.setProxyUsername(detectConfiguration.getHubProxyUsername())
        hubServerConfigBuilder.setProxyPassword(detectConfiguration.getHubProxyPassword())

        hubServerConfigBuilder.setAutoImportHttpsCertificates(detectConfiguration.getHubAutoImportCertificate())
        hubServerConfigBuilder.setLogger(slf4jIntLogger)

        hubServerConfigBuilder.build()
    }
}
