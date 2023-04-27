package com.synopsys.integration.detect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.blackduck.configuration.BlackDuckServerConfigBuilder;
import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

public class ApplicationUpdater {
    public static final String DOWNLOAD_URL = "api/tools/detect";
    public static final String DOWNLOAD_VERSION_HEADER = "Version";
    public static final String INSTALLED_VERSION_FILE_NAME = "version.txt";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntHttpClient intHttpClient;
    private final List<String> argsList;
    private final String blackduckHost;
    private final int indexOfOfflineMode;
    private final DetectInfo detectInfo;

    public ApplicationUpdater(String[] args) {
        argsList = Arrays.asList(args);
        indexOfOfflineMode = argsList.indexOf("blackduck.offline.mode");
        int indexOfBlackduckHost = argsList.indexOf("blackduck.url");
        if (argsList.size() > 1 && indexOfBlackduckHost > -1 && indexOfBlackduckHost + 1 < argsList.size()) {
            blackduckHost = argsList.get(indexOfBlackduckHost + 1);
        } else {
            blackduckHost = null;
        }
        intHttpClient = new IntHttpClient(new SilentIntLogger(),
                BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create(),
                BlackDuckServerConfigBuilder.DEFAULT_TIMEOUT_SECONDS, 
                true, 
                ProxyInfo.NO_PROXY_INFO
        );
        DetectInfoUtility detectInfoUtility = new DetectInfoUtility();
        detectInfo = detectInfoUtility.createDetectInfo();
    }
    
    private boolean canSelfUpdate() {
        String detectSource = System.getenv("DETECT_SOURCE");
        String detectLatestReleaseVersion = System.getenv("DETECT_LATEST_RELEASE_VERSION");
        String detectVersionKey = System.getenv("DETECT_VERSION_KEY");
        return detectSource == null 
                && detectLatestReleaseVersion == null
                && detectVersionKey == null
                && indexOfOfflineMode == -1
                && blackduckHost != null;
    }
    
    private String determineJarDownloadPath() {
        String home, tmp, detectJarDownloadPath, jarDownloadPath;
        if ((detectJarDownloadPath = System.getenv("DETECT_JAR_DOWNLOAD_DIR")) != null) {
            jarDownloadPath = detectJarDownloadPath;
        } else if ((tmp = System.getenv("TMP")) != null) {
            jarDownloadPath = tmp;
        } else if ((home = System.getenv("HOME")) != null) {
            jarDownloadPath = home.endsWith("/")? home.concat("tmp") : home.concat("/tmp");
        } else {
            jarDownloadPath = "./";
        }
        return jarDownloadPath;
    }
    
    protected boolean selfUpdate() {
        boolean canSelfUpdateFlag;
        if (canSelfUpdateFlag = canSelfUpdate()) {
            try {
                String jarDownloadPath = determineJarDownloadPath();
                File newDetectJar = installOrUpdateScanner(jarDownloadPath, "synopsys-detect.jar");
                this.executeRunnableJar(newDetectJar.getAbsolutePath(), argsList);
            } catch (IntegrationException ex) {
                canSelfUpdateFlag = false;
            } catch (Exception ex) {
                canSelfUpdateFlag = false;
            }
        }
        return canSelfUpdateFlag;
    }

    private File installOrUpdateScanner(String dirPath, String fileName) throws IntegrationException {
        File installDirectory = new File(dirPath);
        if (!installDirectory.exists()) {
            installDirectory.mkdirs();
        }
        File detectInstallation = new File(installDirectory, fileName);
        HttpUrl downloadUrl = buildDownloadUrl();
        try {
            Optional<String> currentInstalledVersion = determineInstalledVersion();
            String newInstalledVersion = download(detectInstallation, downloadUrl, currentInstalledVersion.orElse(""));
            if (!detectInstallation.setExecutable(true)) {
                throw new IntegrationException(String.format("Attempt to make %s executable failed.", detectInstallation.getAbsolutePath()));
            }
            updateVersionFile(newInstalledVersion, installDirectory);
            logger.info("Detect was downloaded/found successfully: " + installDirectory.getAbsolutePath());
            return detectInstallation;
        } catch (IntegrationException | IOException e) {
            throw new BlackDuckIntegrationException("Detect could not be downloaded successfully: " + e.getMessage(), e);
        }
    }
    
    private int executeRunnableJar(String jarFullPath, List<String> argsList) throws Exception {
        List<String> commands = Arrays.asList("java", "-jar", jarFullPath);
        commands.addAll(argsList);
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        Process process = processBuilder.start();
        return process.waitFor();
    }

    private HttpUrl buildDownloadUrl() throws IntegrationException {
        StringBuilder url = new StringBuilder(blackduckHost);
        if (!blackduckHost.endsWith("/")) {
            url.append("/");
        }
        url.append(DOWNLOAD_URL);
        return new HttpUrl(url.toString());
    }

    // Downloads Detect from BD.  If successful, returns downloaded version, otherwise throws exception
    private String download(File installDirectory, HttpUrl downloadUrl, String currentVersion) throws IntegrationException, IOException {
        logger.debug(String.format("Downloading artifact to '%s' from '%s'.", installDirectory.getAbsolutePath(), downloadUrl));
        
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(DOWNLOAD_VERSION_HEADER, currentVersion);
        
        Request request = new Request(downloadUrl, HttpMethod.GET, null, queryParams, headers, null);
        
        try (Response response = intHttpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                logger.debug("Deleting existing file.");
                FileUtils.deleteQuietly(installDirectory);
                logger.debug("Writing to file.");
                InputStream jarBytesInputStream = response.getContent();
                FileUtils.copyInputStreamToFile(jarBytesInputStream, installDirectory);
                logger.debug("Successfully wrote response to file.");
                return response.getHeaderValue(DOWNLOAD_VERSION_HEADER);
            } else if (response.getStatusCode() == 304) {
                logger.debug("Present Detect installation is up to date - skipping download.");
                return currentVersion;
            } else {
                logger.trace("Unable to download artifact. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
                throw new IntegrationException("Unable to download artifact. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }
    }

    private void updateVersionFile(String installedVersion, File installDirectory) throws IOException {
        File versionFile = new File(installDirectory, INSTALLED_VERSION_FILE_NAME);
        FileUtils.writeStringToFile(versionFile, installedVersion, StandardCharsets.UTF_8.toString());
    }

    private Optional<String> determineInstalledVersion() {
        String detectVersion = detectInfo.getDetectVersion();
        if (detectVersion != null) {
            return Optional.of(detectInfo.getDetectVersion());
        } else {
            return Optional.empty();
        }
    }
}
