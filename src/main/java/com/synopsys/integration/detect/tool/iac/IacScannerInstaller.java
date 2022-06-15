package com.synopsys.integration.detect.tool.iac;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.api.core.BlackDuckResponse;
import com.synopsys.integration.blackduck.api.core.response.UrlSingleResponse;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.http.BlackDuckRequestBuilder;
import com.synopsys.integration.blackduck.http.client.BlackDuckHttpClient;
import com.synopsys.integration.blackduck.service.request.BlackDuckRequest;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.workflow.file.DirectoryManager;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.response.Response;
import com.synopsys.integration.util.OperatingSystemType;

public class IacScannerInstaller {
    public static final String IAC_SCANNER_DOWNLOAD_URL = "api/tools/sigma";
    public static final String IAC_SCANNER_DOWNLOAD_ARCH_QUERY_PARAMETER_KEY = "arch";
    public static final String WINDOWS_IAC_SCANNER_DOWNLOAD_QUERY_PARAMETER_VALUE = "windows_x86_64";
    public static final String MAC_IAC_SCANNER_DOWNLOAD_QUERY_PARAMETER_VALUE = "macos_x86_64";
    public static final String LINUX_IAC_SCANNER_DOWNLOAD_QUERY_PARAMETER_VALUE = "linux_x86_64";
    public static final String IAC_SCANNER_DOWNLOAD_VERSION_HEADER = "Version";
    public static final String IAC_SCANNER_INSTALLED_VERSION_FILE_NAME = "iac-scanner-version.txt";

    public static final String IAC_SCANNER_INSTALL_DIR_NAME = "iac";
    public static final String IAC_SCANNER_INSTALL_FILE_NAME = "iac";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BlackDuckHttpClient blackDuckHttpClient;
    private final DetectInfo detectInfo;
    private final HttpUrl blackDuckServerUrl;
    private final DirectoryManager directoryManager;

    public IacScannerInstaller(
        BlackDuckHttpClient blackDuckHttpClient, DetectInfo detectInfo,
        HttpUrl blackDuckServerUrl,
        DirectoryManager directoryManager
    ) {
        this.blackDuckHttpClient = blackDuckHttpClient;
        this.detectInfo = detectInfo;
        this.blackDuckServerUrl = blackDuckServerUrl;
        this.directoryManager = directoryManager;
    }

    public File installOrUpdateScanner() throws IntegrationException {
        File installDirectory = directoryManager.getPermanentDirectory(IAC_SCANNER_INSTALL_DIR_NAME);
        installDirectory.mkdirs();
        File iacScannerInstallation = new File(installDirectory, IAC_SCANNER_INSTALL_FILE_NAME);
        HttpUrl downloadUrl = buildDownloadUrl();
        try {
            Optional<String> currentInstalledVersion = determineInstalledVersion(installDirectory);
            String newInstalledVersion = download(iacScannerInstallation, downloadUrl, currentInstalledVersion.orElse("")); // if we pass empty string, will trigger download
            if (!iacScannerInstallation.setExecutable(true)) {
                throw new IntegrationException(String.format("Attempt to make %s executable failed.", iacScannerInstallation.getAbsolutePath()));
            }
            updateVersionFile(newInstalledVersion, installDirectory);
            logger.info("Iac Scanner was downloaded/found successfully: " + installDirectory.getAbsolutePath());
            return iacScannerInstallation;
        } catch (Exception e) {
            throw new BlackDuckIntegrationException("Iac Scanner could not be downloaded successfully: " + e.getMessage(), e);
        }
    }

    private HttpUrl buildDownloadUrl() throws IntegrationException {
        StringBuilder url = new StringBuilder(blackDuckServerUrl.string());
        if (!blackDuckServerUrl.string().endsWith("/")) {
            url.append("/");
        }
        url.append(IAC_SCANNER_DOWNLOAD_URL);
        return new HttpUrl(url.toString());
    }

    private String determineDownloadArchQueryParameter() {
        if (detectInfo.getCurrentOs().equals(OperatingSystemType.MAC)) {
            return MAC_IAC_SCANNER_DOWNLOAD_QUERY_PARAMETER_VALUE;
        } else if (detectInfo.getCurrentOs().equals(OperatingSystemType.WINDOWS)) {
            return WINDOWS_IAC_SCANNER_DOWNLOAD_QUERY_PARAMETER_VALUE;
        } else {
            return LINUX_IAC_SCANNER_DOWNLOAD_QUERY_PARAMETER_VALUE;
        }
    }

    // Downloads Iac Scanner from BD.  If successful, returns downloaded version, otherwise throws exception
    private String download(File installDirectory, HttpUrl downloadUrl, String currentVersion) throws IntegrationException, IOException {
        logger.debug(String.format("Downloading artifact to '%s' from '%s'.", installDirectory.getAbsolutePath(), downloadUrl));
        BlackDuckRequestBuilder requestBuilder = new BlackDuckRequestBuilder()
            .url(downloadUrl)
            .addHeader(IAC_SCANNER_DOWNLOAD_VERSION_HEADER, currentVersion)
            .addQueryParameter(IAC_SCANNER_DOWNLOAD_ARCH_QUERY_PARAMETER_KEY, determineDownloadArchQueryParameter());
        BlackDuckRequest<BlackDuckResponse, UrlSingleResponse<BlackDuckResponse>> request = BlackDuckRequest.createSingleRequest(
            requestBuilder,
            downloadUrl,
            BlackDuckResponse.class
        );
        try (Response response = blackDuckHttpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Deleting existing file.");
                FileUtils.deleteQuietly(installDirectory);
                logger.debug("Writing to file.");
                InputStream jarBytesInputStream = response.getContent();
                FileUtils.copyInputStreamToFile(jarBytesInputStream, installDirectory);
                logger.debug("Successfully wrote response to file.");
                return response.getHeaderValue(IAC_SCANNER_DOWNLOAD_VERSION_HEADER);
            } else if (response.getStatusCode() == 304) {
                logger.debug("Present Iac Scanner installation is up to date - skipping download.");
                return currentVersion;
            } else {
                logger.trace("Unable to download artifact. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException("Unable to download artifact. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }
    }

    private void updateVersionFile(String installedVersion, File installDirectory) throws IOException {
        File versionFile = new File(installDirectory, IAC_SCANNER_INSTALLED_VERSION_FILE_NAME);
        FileUtils.writeStringToFile(versionFile, installedVersion, Charset.defaultCharset());
    }

    private Optional<String> determineInstalledVersion(File installDirectory) throws IOException {
        File versionFile = new File(installDirectory, IAC_SCANNER_INSTALLED_VERSION_FILE_NAME);
        if (versionFile.exists()) {
            return Optional.of(FileUtils.readFileToString(versionFile, Charset.defaultCharset()));
        } else {
            return Optional.empty();
        }
    }
}
