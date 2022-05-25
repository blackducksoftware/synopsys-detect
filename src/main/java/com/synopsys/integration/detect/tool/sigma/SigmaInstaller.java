package com.synopsys.integration.detect.tool.sigma;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.ArtifactResolver;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.util.OperatingSystemType;

public class SigmaInstaller {
    public static final String WINDOWS_SIGMA_DOWNLOAD_URL_SUFFIX = "download/sigma-windows_x86_64";
    public static final String MAC_SIGMA_DOWNLOAD_URL_SUFFIX = "download/sigma-macos_x86_64";
    public static final String LINUX_SIGMA_DOWNLOAD_URL_SUFFIX = "download/sigma-linux_x86_64";

    public static final String SIGMA_INSTALL_DIR_NAME = "sigma";
    public static final String SIGMA_INSTALL_FILE_NAME = "sigma";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ArtifactResolver artifactResolver;
    private final DetectInfo detectInfo;
    private final HttpUrl blackDuckServerUrl;
    private final DirectoryManager directoryManager;

    public SigmaInstaller(
        ArtifactResolver artifactResolver,
        DetectInfo detectInfo,
        HttpUrl blackDuckServerUrl,
        DirectoryManager directoryManager
    ) {
        this.detectInfo = detectInfo;
        this.artifactResolver = artifactResolver;
        this.blackDuckServerUrl = blackDuckServerUrl;
        this.directoryManager = directoryManager;
    }

    public File installOrUpdateScanner() throws BlackDuckIntegrationException {
        File installDirectory = directoryManager.getPermanentDirectory(SIGMA_INSTALL_DIR_NAME);
        installDirectory.mkdirs();
        File sigmaInstallation = new File(installDirectory, SIGMA_INSTALL_FILE_NAME);
        String downloadUrl = determineDownloadUrl();
        try {
            sigmaInstallation.createNewFile(); //TODO- investigate if we actually need to create file first
            if (shouldDownload()) {
                logger.info("Sigma installation is not up to date.  Will download Sigma."); //TODO- update log message when "am I current" criteria is clear
                artifactResolver.downloadArtifact(sigmaInstallation, downloadUrl);
                sigmaInstallation.setExecutable(true);
                return sigmaInstallation;
            }
        } catch (Exception e) {
            throw new BlackDuckIntegrationException("Sigma could not be downloaded successfully: " + e.getMessage(), e);
        }

        logger.info("Sigma was downloaded/found successfully: " + installDirectory.getAbsolutePath());
        return sigmaInstallation;
    }

    private String determineDownloadUrl() {
        StringBuilder url = new StringBuilder(blackDuckServerUrl.string());
        if (!blackDuckServerUrl.string().endsWith("/")) {
            url.append("/");
        }

        if (detectInfo.getCurrentOs().equals(OperatingSystemType.MAC)) {
            url.append(MAC_SIGMA_DOWNLOAD_URL_SUFFIX);
        } else if (detectInfo.getCurrentOs().equals(OperatingSystemType.WINDOWS)) {
            url.append(WINDOWS_SIGMA_DOWNLOAD_URL_SUFFIX);
        } else {
            url.append(LINUX_SIGMA_DOWNLOAD_URL_SUFFIX);
        }

        return url.toString();
    }

    private boolean shouldDownload() {
        //TODO- query "am I current" endpoint
        return true;

    }
}
