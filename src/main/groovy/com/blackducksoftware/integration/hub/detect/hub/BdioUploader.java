package com.blackducksoftware.integration.hub.detect.hub;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.configuration.HubServerConfig;
import com.blackducksoftware.integration.hub.detect.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.model.DetectProject;
import com.blackducksoftware.integration.hub.service.CodeLocationService;

import groovy.transform.TypeChecked;

@Component
@TypeChecked
public class BdioUploader {
    private final Logger logger = LoggerFactory.getLogger(BdioUploader.class);

    @Autowired
    private DetectConfiguration detectConfiguration;

    public void uploadBdioFiles(final HubServerConfig hubServerConfig, final CodeLocationService codeLocationService, final DetectProject detectProject, final List<File> createdBdioFiles) throws IntegrationException {
        for (final File file : createdBdioFiles) {
            logger.info(String.format("uploading %s to %s", file.getName(), detectConfiguration.getHubUrl()));
            codeLocationService.importBomFile(file);
            if (detectConfiguration.getCleanupBdioFiles()) {
                file.delete();
            }
        }
    }

}
