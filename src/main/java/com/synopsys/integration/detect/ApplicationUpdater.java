package com.synopsys.integration.detect;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
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
import com.synopsys.integration.log.Slf4jIntLogger;
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
    private String blackduckHost = null;
    private String offlineMode = null;
    private final DetectInfo detectInfo;
    private final static String BLACKDUCK_OFFLINE_MODE_ARG = "blackduck.offline.mode";
    private final static String BLACKDUCK_URL_ARG = "blackduck.url";
    
    private void parseArguments() {
        ListIterator<String> it = argsList.listIterator();
        while (it.hasNext()) {
            String argument = it.next();
            if (argument.contains(BLACKDUCK_OFFLINE_MODE_ARG)) {
                offlineMode = findArgument(it, argument);
            } else if (argument.contains(BLACKDUCK_URL_ARG)) {
                blackduckHost = findArgument(it, argument);
            }
        }
        //blackduckHost = "https://prd-kb-match-dev-hub01.dc2.lan";
    }
    
    private String findArgument(ListIterator<String> it, String argument) {
        int equalsIndex;
        if ((equalsIndex = argument.indexOf("=")) > -1) {
            return argument.substring(equalsIndex + 1, argument.length());
        } else if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public ApplicationUpdater(String[] args) {
        argsList = Arrays.asList(args);
        parseArguments();
        intHttpClient = new IntHttpClient(new Slf4jIntLogger(logger),
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
                && offlineMode == null
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
        if (canSelfUpdate()) {
            try {
                String jarDownloadPath = determineJarDownloadPath();
                File newDetectJar = installOrUpdateScanner(jarDownloadPath, "synopsys-detect.jar");
                if (newDetectJar != null) {
                    this.executeRunnableJar(newDetectJar.getAbsolutePath(), argsList);
                    return true;
                }
            } catch (IntegrationException ex) {
                logger.error(ex.getMessage());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                return true;
            }
        }
        return false;
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
            if (newInstalledVersion != null) {
                if (!detectInstallation.setExecutable(true)) {
                    throw new IntegrationException(String.format("Attempt to make %s executable failed.", detectInstallation.getAbsolutePath()));
                }
                updateVersionFile(newInstalledVersion, installDirectory);
                logger.info("Detect was downloaded/found successfully: " + installDirectory.getAbsolutePath());
                return detectInstallation;
            }
        } catch (IntegrationException | IOException e) {
            throw new BlackDuckIntegrationException("Detect could not be downloaded successfully: " + e.getMessage(), e);
        }
        return null;
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
                try (InputStream jarInputStream = response.getContent()) {
                    Files.copy(jarInputStream, installDirectory.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                logger.debug("Successfully wrote response to file.");
                return response.getHeaderValue(DOWNLOAD_VERSION_HEADER);
            } else if (response.getStatusCode() == 304) {
                logger.debug("Present Detect installation is up to date - skipping download.");
            } else {
                logger.trace("Unable to download artifact. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }
        return null;
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
