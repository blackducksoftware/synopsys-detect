package com.synopsys.integration.detect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.Set;

import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.credentials.Credentials;
import com.synopsys.integration.rest.credentials.CredentialsBuilder;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.proxy.ProxyInfoBuilder;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

import freemarker.template.Version;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationUpdater extends URLClassLoader {
    
    protected static final String DOWNLOAD_VERSION_HEADER = "Version";
    private static final String LOG_PREFIX = "Detect-Self-Updater: ";
    private static final String DOWNLOAD_URL = "api/tools/detect";
    private static final String DOWNLOADED_FILE_NAME = "X-Artifactory-Filename";
    private static final Version MINIMUM_DETECT_VERSION = new Version(8, 9, 0);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String blackduckHost = null;
    private String offlineMode = null;
    private boolean trustCertificate = false;
    private Set<String> proxyIgnoredHosts = new HashSet<>();
    private final String[] args;
    private final DetectInfo detectInfo;
    private final Map<String, String> proxyProperties;
    private final ApplicationUpdaterUtility applicationUpdaterUtility;
    
    private final static String SYS_ENV_PROP_BLACKDUCK_URL = "BLACKDUCK_URL";
    
    private final static String SYS_ENV_PROP_DETECT_SOURCE = "DETECT_SOURCE";
    private final static String SYS_ENV_PROP_DETECT_LATEST_RELEASE_VERSION = "DETECT_LATEST_RELEASE_VERSION";
    private final static String SYS_ENV_PROP_DETECT_VERSION_KEY = "DETECT_VERSION_KEY";
    
    private final static String SYS_ENV_PROP_PROXY_HTTP_HOST = "http.proxyHost";
    private final static String SYS_ENV_PROP_PROXY_HTTP_PORT = "http.proxyPort";
    private final static String SYS_ENV_PROP_PROXY_HTTP_USERNAME = "http.proxyUsername";
    private final static String SYS_ENV_PROP_PROXY_HTTP_PASSWORD = "http.proxyPassword";
    
    private final static String SYS_ENV_PROP_PROXY_HTTPS_HOST = "https.proxyHost";
    private final static String SYS_ENV_PROP_PROXY_HTTPS_PORT = "https.proxyPort";
    private final static String SYS_ENV_PROP_PROXY_HTTPS_USERNAME = "https.proxyUsername";
    private final static String SYS_ENV_PROP_PROXY_HTTPS_PASSWORD = "https.proxyPassword";
    
    private final static String ARG_BLACKDUCK_OFFLINE_MODE = "blackduck.offline.mode";
    private final static String ARG_BLACKDUCK_URL = "blackduck.url";
    private final static String ARG_TRUST_CERTIFICATE = "blackduck.trust.cert";
    
    private final static String ARG_PROXY_HOST = "blackduck.proxy.host";
    private final static String ARG_PROXY_IGNORED_HOSTS = "blackduck.proxy.ignored.hosts";
    private final static String ARG_PROXY_NTLM_DOMAIN = "blackduck.proxy.ntlm.domain";
    private final static String ARG_PROXY_NTLM_WORKSTATION = "blackduck.proxy.ntlm.workstation";
    private final static String ARG_PROXY_PASSWORD = "blackduck.proxy.password";
    private final static String ARG_PROXY_PORT = "blackduck.proxy.port";
    private final static String ARG_PROXY_USERNAME = "blackduck.proxy.username";
    
    public ApplicationUpdater(ApplicationUpdaterUtility applicationUpdaterUtility, String[] args) {
        super(new URL[] {}, Thread.currentThread().getContextClassLoader());
        // System Environment Properties are checked before application arguments.
        proxyProperties = new HashMap<>(7);
        proxyIgnoredHosts = new HashSet<>();
        checkEnvironmentProperties();
        this.args = parseArguments(args);
        this.applicationUpdaterUtility = applicationUpdaterUtility;
        detectInfo = new DetectInfoUtility().createDetectInfo();
    }
    
    public boolean selfUpdate() {
        if (canSelfUpdate()) {
            try {
                final String jarDownloadPath = determineJarDownloadPath();
                final File newDetectJar = installOrUpdateScanner(jarDownloadPath);
                if (newDetectJar != null) {
                    runMainClass(newDetectJar.toPath(), args);
                    return true;
                }
            } catch (
                    IntegrationException 
                            | AccessDeniedException
                            | ClassNotFoundException 
                            | IllegalAccessException 
                            | IllegalArgumentException 
                            | InstantiationException 
                            | NoSuchMethodException 
                            | InvocationTargetException ex) {
                logger.error("{} Self-Update of Detect failed due to {}. "
                        + "Detect will now continue with existing version.", 
                        LOG_PREFIX, ex);
            } catch (IOException ex) {
                logger.error("{} Self-Update of Detect failed due to {}. "
                        + "Detect will now continue with existing version.", 
                        LOG_PREFIX, ex);
            }
        }
        return false;
    }
    
    private File installOrUpdateScanner(String dirPath) throws AccessDeniedException, IOException, IntegrationException {
        final File installDirectory = new File(dirPath);
        
        if (!installDirectory.exists()) {
            installDirectory.mkdir();
        } else if (!checkInstallationDir(installDirectory.toPath())) {
            return null;
        }
        
        final HttpUrl downloadUrl = buildDownloadUrl();
        final Optional<String> currentInstalledVersion = determineInstalledVersion();
        final Path newJar = download(installDirectory, downloadUrl, currentInstalledVersion.orElse(""));
        
        if (newJar != null) {
            final File newJarFile = newJar.toFile();
            final String newFileName = newJar.getFileName().toString();
            
            if (isValidDetectFileName(newFileName)) {
                final String newVersionString = getVersionFromDetectFileName(newFileName);
                logger.debug("{} New File Name: {}, new version string: {}", LOG_PREFIX, newFileName, newVersionString);
                return validateDownloadedJar(newJarFile, newVersionString, currentInstalledVersion.orElse(""));
            }
        }
        return null;
    }
    
    private File validateDownloadedJar(File newJarFile, String newVersionString, String currentVersion) throws IntegrationException {
        if (StringUtils.isNotBlank(newVersionString)
                && !newVersionString.equals(currentVersion)
                && !isDownloadVersionTooOld(currentVersion, newVersionString)) {
            if (newJarFile.setExecutable(true)) {
                logger.info("{} Centrally managed version of Detect was downloaded successfully and is ready to be run: {}", LOG_PREFIX, newJarFile.getAbsolutePath());
                return newJarFile;
            } else {
                throw new IntegrationException(String.format("Failed to make %s executable. Please permissions of the parent directory and the file.", newJarFile.getAbsolutePath()));
            }
        }
        return null;
    }
    
    private void runMainClass(Path jarPath, String[] launchArgs) 
            throws 
            NoSuchMethodException, 
            InstantiationException, 
            IllegalAccessException, 
            IllegalArgumentException, 
            InvocationTargetException, 
            IOException,
            ClassNotFoundException {
        String argumentsText = Arrays.stream(launchArgs).collect(Collectors.joining(" "));
        logger.debug("{} Ready to run the downloaded Detect JAR at {} with the same arguments.", 
                LOG_PREFIX, jarPath, argumentsText);
        try {
            String pathToJar = jarPath.toAbsolutePath().toString();
            final Map<String, Class<?>> classMap = new HashMap<>();
            try (JarFile jarFile = new JarFile(pathToJar)) {
                final Enumeration<JarEntry> jarEntryEnum = jarFile.entries();
                final URL[] urls = { new URL("jar:file:" + pathToJar + "!/") };
                try (final URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls)) {
                    while (jarEntryEnum.hasMoreElements()) {
                        final JarEntry jarEntry = jarEntryEnum.nextElement();
                        String jarEntryName = jarEntry.getName();
                        if (jarEntry.getName().startsWith("org/springframework/boot")  
                                && jarEntry.getName().endsWith(".class") == true) {
                            int endIndex = jarEntryName.lastIndexOf(".class");
                            String className = jarEntryName.substring(0, endIndex).replace('/', '.');
                            final Class<?> loadedClass = urlClassLoader.loadClass(className);
                            classMap.put(loadedClass.getName(), loadedClass);
                        }
                    }
                }
            }
            final Class<?> jarFileArchiveClass = classMap.get("org.springframework.boot.loader.archive.JarFileArchive");
            final Constructor<?> jarFileArchiveConstructor = jarFileArchiveClass.getConstructor(File.class);
            final Object jarFileArchive = jarFileArchiveConstructor.newInstance(new File(pathToJar));
            final Class<?> archiveClass = classMap.get("org.springframework.boot.loader.archive.Archive");
            final Class mainClass = classMap.get("org.springframework.boot.loader.JarLauncher");
            final Constructor<?> jarLauncherConstructor = mainClass.getDeclaredConstructor(archiveClass);
            jarLauncherConstructor.setAccessible(true);
            final Object jarLauncher = jarLauncherConstructor.newInstance(jarFileArchive);
            final Class<?> launcherClass = 	classMap.get("org.springframework.boot.loader.Launcher");
            final Method launchMethod = launcherClass.getDeclaredMethod("launch", String[].class);
            launchMethod.setAccessible(true);
            launchMethod.invoke(jarLauncher, new Object[]{args});
        } finally {
            close();
        }
    }
    
    private boolean checkInstallationDir(Path path) throws NoSuchFileException, AccessDeniedException, IOException {
        final Path parentPath = path.getParent();
        final Path directoryPath = Files.createDirectories(parentPath);
        
        if (!Files.exists(directoryPath)) {
            String parentPathString = (parentPath != null) ? parentPath.toString() : null;
            throw new NoSuchFileException(parentPathString, path.toString(), 
                    "Unable to locate the installation directory.");
        } else if (Files.exists(path)) {
            if (Files.isWritable(path)) {
                return true;
            } else {
                throw new AccessDeniedException(path.toString(), null, 
                        "No write permisison to store downloaded Jar in the installation directory: "
                                .concat(path.getFileName().toAbsolutePath().toString()));
            }
        }
        return false;
    }
    
    private void checkEnvironmentProperties() {
        this.blackduckHost = System.getenv(SYS_ENV_PROP_BLACKDUCK_URL);
        final String httpsHost = System.getenv(SYS_ENV_PROP_PROXY_HTTPS_HOST);
        final String httpHost = System.getenv(SYS_ENV_PROP_PROXY_HTTP_HOST);
        
        if (httpsHost != null && StringUtils.isNotBlank(httpsHost)) {
            /* HTTPS Proxy System Environment settings are preferred. */
            proxyProperties.put(ARG_PROXY_PORT, httpsHost);
            proxyProperties.put(ARG_PROXY_PORT, System.getenv(SYS_ENV_PROP_PROXY_HTTPS_PORT));
            proxyProperties.put(ARG_PROXY_PORT, System.getenv(SYS_ENV_PROP_PROXY_HTTPS_USERNAME));
            proxyProperties.put(ARG_PROXY_PORT, System.getenv(SYS_ENV_PROP_PROXY_HTTPS_PASSWORD));
        } else if (httpHost != null && StringUtils.isNotBlank(httpHost)) {
            /* If not HTTPS, then HTTP is fine. But Log a warning. */
            logger.warn("{} Use of HTTP instead of HTTPS for Proxy system environment properties was found. "
                    + "It is strongly recommended to use HTTPS over HTTP for increased security."
                    + "Detect will continue self update check with the configured HTTP settings.");
            proxyProperties.put(ARG_PROXY_PORT, httpHost);
            proxyProperties.put(ARG_PROXY_PORT, System.getenv(SYS_ENV_PROP_PROXY_HTTP_PORT));
            proxyProperties.put(ARG_PROXY_PORT, System.getenv(SYS_ENV_PROP_PROXY_HTTP_USERNAME));
            proxyProperties.put(ARG_PROXY_PORT, System.getenv(SYS_ENV_PROP_PROXY_HTTP_PASSWORD));
        }
    }
    
    /**
     * Any proxy setting arguments found are loaded into a temporary map so that 
     * they can be discarded entirely if the proxy host and port are not set in the arguments. 
     * Note that the arguments can come in any order. This way, incomplete proxy 
     * argument settings do not overwrite complete proxy settings set through 
     * system environment properties.
     * @param args
     * @return 
     */
    private String[] parseArguments(String[] args) {
        final Map<String, String> tempProxyProperties = new HashMap<>(7);
        final ListIterator<String> it = Arrays.asList(args).listIterator();
        while (it.hasNext()) {
            String argument = it.next();
            if (argument.contains(ARG_BLACKDUCK_OFFLINE_MODE)) {
                offlineMode = findArgumentValue(it, argument);
            } else if (argument.contains(ARG_BLACKDUCK_URL)) {
                blackduckHost = findArgumentValue(it, argument);
            } else if (argument.contains(ARG_TRUST_CERTIFICATE)) {
                trustCertificate = findArgumentValue(it, argument).toLowerCase().equals("true");
            } else if (argument.contains(ARG_PROXY_HOST)) {
                addProxyPropertyToTempMap(ARG_PROXY_HOST, it, argument, tempProxyProperties);
            } else if (argument.contains(ARG_PROXY_PORT)) {
                addProxyPropertyToTempMap(ARG_PROXY_PORT, it, argument, tempProxyProperties);
            } else if (argument.contains(ARG_PROXY_PASSWORD)) {
                addProxyPropertyToTempMap(ARG_PROXY_PASSWORD, it, argument, tempProxyProperties);
            } else if (argument.contains(ARG_PROXY_USERNAME)) {
                addProxyPropertyToTempMap(ARG_PROXY_USERNAME, it, argument, tempProxyProperties);
            } else if (argument.contains(ARG_PROXY_NTLM_DOMAIN)) {
                addProxyPropertyToTempMap(ARG_PROXY_NTLM_DOMAIN, it, argument, tempProxyProperties);
            } else if (argument.contains(ARG_PROXY_NTLM_WORKSTATION)) {
                addProxyPropertyToTempMap(ARG_PROXY_NTLM_WORKSTATION, it, argument, tempProxyProperties);
            } else if (argument.contains(ARG_PROXY_IGNORED_HOSTS)) {
                proxyIgnoredHosts = findArgumentCommaDelimitedValues(it, argument);
            }
        }
        
        if (tempProxyProperties.keySet().contains(ARG_PROXY_HOST) 
                && tempProxyProperties.keySet().contains(ARG_PROXY_PORT)) {
            for (String key : tempProxyProperties.keySet()) {
                proxyProperties.put(key, tempProxyProperties.get(key));
            }
        }
        return args;
    }
    
    /**
     * Add one proxy setting from argument to the temporary map.
     * @param argKey
     * @param it
     * @param argument
     * @param tempProxyProperties 
     */
    private void addProxyPropertyToTempMap(String argKey,
            ListIterator<String> it, 
            String argument, 
            Map<String, String> tempProxyProperties) {
        final String value = findArgumentValue(it, argument);
        tempProxyProperties.put(argKey, value);
    }
    
    private String findArgumentValue(ListIterator<String> it, String argument) {
        final int equalsIndex;
        if ((equalsIndex = argument.indexOf("=")) > -1) {
            return argument.substring(equalsIndex + 1, argument.length());
        } else if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
    
    /**
     * To load the comma separated values of an argument, in this case - that of the ignored hosts for proxy argument.
     * @param it List iterator of the arguments. This is used to choose between an assigned value (--arg=value) or a space delimited value (--args value).
     * @param argument The argument
     * @return The set of values of the argument.
     */
    private Set<String> findArgumentCommaDelimitedValues(ListIterator<String> it, String argument) {
        final int equalsIndex;
        final String delimitedValues;
        if ((equalsIndex = argument.indexOf("=")) > -1) {
            delimitedValues = argument.substring(equalsIndex + 1, argument.length());
            return Arrays.stream(delimitedValues.split("\\,"))
                .collect(Collectors.toSet());
        } else if (it.hasNext()) {
            delimitedValues = it.next();
            return Arrays.stream(delimitedValues.split("\\,"))
                .collect(Collectors.toSet());
        }
        return null;
    }
    
    private void addConditionalLogMessageForSysEnvProp(List<String> logMessages, String envProperty, String envValue) {
        if (envValue != null) {
            logMessages.add("The presence of the system environment property "
                    .concat(envProperty)
                    .concat("=")
                    .concat(envValue)
                    .concat(" indicates possible use of a Detect Auto-upgrade script, "
                            + "which is redundant with the Self-Update feature."));
        }
    }
    
    private boolean canSelfUpdate() {
        final String detectSource = System.getenv(SYS_ENV_PROP_DETECT_SOURCE);
        final String detectLatestReleaseVersion = System.getenv(SYS_ENV_PROP_DETECT_LATEST_RELEASE_VERSION);
        final String detectVersionKey = System.getenv(SYS_ENV_PROP_DETECT_VERSION_KEY);
        final List<String> logMessages = new LinkedList<>();
        final String message = "Self-Update feature is disabled because of the following reasons:";
        addConditionalLogMessageForSysEnvProp(logMessages, SYS_ENV_PROP_DETECT_SOURCE, detectSource);
        addConditionalLogMessageForSysEnvProp(logMessages, SYS_ENV_PROP_DETECT_LATEST_RELEASE_VERSION, detectLatestReleaseVersion);
        addConditionalLogMessageForSysEnvProp(logMessages, SYS_ENV_PROP_DETECT_VERSION_KEY, detectVersionKey);
        
        if (offlineMode != null && offlineMode.toLowerCase().equals("true")) {
            logMessages.add("The presence of the input argument --"
                    .concat(ARG_BLACKDUCK_OFFLINE_MODE)
                    .concat("=true to run Detect in offline mode is incompatible with the Self-Update feature."));
        }
        
        if (blackduckHost == null) {
            logMessages.add("The absence of the input argument --"
                    .concat(ARG_BLACKDUCK_URL)
                    .concat(" to specify a Black Duck server is incompatible with the Self-Update feature."));
        }
        
        if(!logMessages.isEmpty()) {
            logMessages.add(0, message);
            for (String logMessage : logMessages) {
                logger.warn("{} ".concat(logMessage), LOG_PREFIX);
            }
            return false;
        }
        return true;
    }
    
    private boolean isDownloadVersionTooOld(String currentVersion, String downloadVersionString) {
        final Version downloadVersion = convert(downloadVersionString);
        if (downloadVersion.getMajor() < MINIMUM_DETECT_VERSION.getMajor()
            || (downloadVersion.getMajor() == MINIMUM_DETECT_VERSION.getMajor() 
                && (downloadVersion.getMinor() < MINIMUM_DETECT_VERSION.getMinor()
                || (downloadVersion.getMinor() == MINIMUM_DETECT_VERSION.getMinor()
                    && (downloadVersion.getMicro() < MINIMUM_DETECT_VERSION.getMicro()))))) {
            logger.warn("{} The Detect version {} mapped at Black Duck server is "
                    + "not eligible for downgrade from the current version of {}. "
                    + "The self-update feature is available from {} onwards.", 
                    LOG_PREFIX, downloadVersionString, currentVersion, MINIMUM_DETECT_VERSION);
            return true;
        }
        logger.warn("{} The Detect version {} mapped at Black Duck server is "
                + "eligible for updating the current version of {} as the self-update feature is available in mapped version. "
                + "The self-update feature is available from {} onwards.", 
                LOG_PREFIX, downloadVersionString, currentVersion, MINIMUM_DETECT_VERSION);
        return false;
    }
    
    private HttpUrl buildDownloadUrl() throws IntegrationException {
        final StringBuilder url = new StringBuilder(blackduckHost);
        if (!blackduckHost.endsWith("/")) {
            url.append("/");
        }
        url.append(DOWNLOAD_URL);
        return new HttpUrl(url.toString());
    }
    
    private ProxyInfo prepareProxyInfo() {
        final ProxyInfoBuilder proxyInfoBuilder = new ProxyInfoBuilder();
        final CredentialsBuilder credentialsBuilder = Credentials.newBuilder();
        credentialsBuilder.setUsernameAndPassword(proxyProperties.get(ARG_PROXY_USERNAME), 
                proxyProperties.get(ARG_PROXY_PASSWORD));
        proxyInfoBuilder.setCredentials(credentialsBuilder.build());
        proxyInfoBuilder.setHost(proxyProperties.get(ARG_PROXY_HOST));
        proxyInfoBuilder.setPort(Integer.parseInt(proxyProperties.get(ARG_PROXY_PORT)));
        proxyInfoBuilder.setNtlmDomain(proxyProperties.get(ARG_PROXY_NTLM_DOMAIN));
        proxyInfoBuilder.setNtlmWorkstation(proxyProperties.get(ARG_PROXY_NTLM_WORKSTATION));
        return proxyInfoBuilder.build();
    }

    private Path download(File installDirectory, HttpUrl downloadUrl, String currentVersion) throws IntegrationException, IOException {
        logger.info("{} Checking {} API for centrally managed Detect version to download to {}.", 
                LOG_PREFIX, downloadUrl, installDirectory.getAbsolutePath());
        final Map <String, String> headers = new HashMap<>();
        final Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(DOWNLOAD_VERSION_HEADER, currentVersion);
        final Request request = new Request(downloadUrl, HttpMethod.GET, 
                null, queryParams, headers, null);
        
        /*  Check if the BD host is on the ignore-host-for-proxy list or no proxy 
        *   settings are found to be configured. Then, opt for No Proxy. 
        *   Otherwise, prepare the proxy information from the identified proxy settings. */
        final ProxyInfo proxyInfo;
        if (proxyIgnoredHosts.contains(blackduckHost) || proxyProperties.isEmpty()) {
            proxyInfo = ProxyInfo.NO_PROXY_INFO;
        } else {
            proxyInfo = prepareProxyInfo();
        }
        final IntHttpClient intHttpClient = applicationUpdaterUtility.getIntHttpClient(trustCertificate, proxyInfo);
        try (final Response response = intHttpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                final String newFileName = response.getHeaderValue(DOWNLOADED_FILE_NAME);
                final Path targetFilePath = Paths.get(installDirectory.getAbsolutePath(), "/", newFileName);
                final File targetFile = targetFilePath.toFile();
                
                if (!Files.exists(targetFilePath, LinkOption.NOFOLLOW_LINKS)) {
                    logger.debug("{} Writing to file {}.", LOG_PREFIX, targetFilePath.toAbsolutePath());
                    try(final ReadableByteChannel readableByteChannel = Channels.newChannel(response.getContent())) {
                        try(final FileOutputStream fileOutputStream = new FileOutputStream(targetFile)) {
                            fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                            logger.debug("{} Successfully wrote response to file {}.", LOG_PREFIX, targetFilePath.toAbsolutePath());
                            fileOutputStream.close();
                        }
                        readableByteChannel.close();
                    }
                }
                return targetFilePath;
            } else if (response.getStatusCode() == 304) {
                logger.info("{} Present Detect installation is up to date - skipping download.", LOG_PREFIX);
            } else {
                logger.warn("{} Unable to download artifact. Response code: {} {}", 
                        LOG_PREFIX, response.getStatusCode(), response.getStatusMessage());
            }
        }
        return null;
    }

    private Optional<String> determineInstalledVersion() {
        final String detectVersion = detectInfo.getDetectVersion();
        if (detectVersion != null) {
            return Optional.of(detectInfo.getDetectVersion());
        } else {
            return Optional.empty();
        }
    }
    
    protected String determineJarDownloadPath() {
        final String home, tmp, detectJarDownloadPath, jarDownloadPath;
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
    
    protected Version convert(String versionString) {
        versionString = getVersionFromDetectFileName(versionString);
        final List<Integer> versionParts = Arrays.stream(versionString.split("\\."))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        final Version versionObj;
        switch(versionParts.size()) {
            case 1:
               versionObj = new Version(
                       versionParts.get(0), 
                       0, 
                       0);
               break;
            case 2:
               versionObj = new Version(
                       versionParts.get(0), 
                       versionParts.get(1), 
                       0);
               break;
            default:
               versionObj = new Version(
                       versionParts.get(0), 
                       versionParts.get(1), 
                       versionParts.get(2));
        }
        return versionObj;
    }
    
    protected boolean isValidDetectFileName(String newFileName) {
        return newFileName.lastIndexOf("-") > -1 && newFileName.lastIndexOf(".") > 0;
    }
    
    protected String getVersionFromDetectFileName(String newFileName) {
        return newFileName.replaceAll(".*?((?<!\\w)\\d+([.]\\d+)*).*", "$1");
    }
    
    protected void closeUpdater() {
        try {
            this.close();
        } catch (IOException ex) {
            logger.error("{} Failed to close the ApplicationUpdater gracefully. ", LOG_PREFIX, ex);
        }
    }
}
