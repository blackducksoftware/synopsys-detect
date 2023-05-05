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

import com.synopsys.integration.blackduck.service.BlackDuckServicesFactory;
import com.synopsys.integration.detect.configuration.DetectInfo;
import com.synopsys.integration.detect.configuration.DetectInfoUtility;
import com.synopsys.integration.exception.IntegrationException;
import com.synopsys.integration.log.LogLevel;
import com.synopsys.integration.log.SilentIntLogger;
import com.synopsys.integration.rest.HttpMethod;
import com.synopsys.integration.rest.HttpUrl;
import com.synopsys.integration.rest.client.IntHttpClient;
import com.synopsys.integration.rest.proxy.ProxyInfo;
import com.synopsys.integration.rest.request.Request;
import com.synopsys.integration.rest.response.Response;

import freemarker.template.Version;
import java.util.LinkedList;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationUpdater extends URLClassLoader {
    
    private static final String LOG_PREFIX = "Detect-Self-Updater: ";
    private static final String DOWNLOAD_URL = "api/tools/detect";
    private static final String DOWNLOAD_VERSION_HEADER = "Version";
    private static final String DOWNLOADED_FILE_NAME = "X-Artifactory-Filename";
    private static final Version MINIMUM_DETECT_VERSION = new Version(8, 9, 0);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntHttpClient intHttpClient;
    private final String[] args;
    private String blackduckHost = null;
    private String offlineMode = null;
    private final DetectInfo detectInfo;
    
    private final static String DETECT_SOURCE_SYS_ENV_PROP = "DETECT_SOURCE";
    private final static String DETECT_LATEST_RELEASE_VERSION_SYS_ENV_PROP = "DETECT_LATEST_RELEASE_VERSION";
    private final static String DETECT_VERSION_KEY_SYS_ENV_PROP = "DETECT_VERSION_KEY";
    private final static String BLACKDUCK_OFFLINE_MODE_ARG = "blackduck.offline.mode";
    private final static String BLACKDUCK_URL_ARG = "blackduck.url";
    
    public ApplicationUpdater(String[] args) {
        super(new URL[] {}, Thread.currentThread().getContextClassLoader());
        this.args = parseArguments(args);
        final SilentIntLogger silentLogger = new SilentIntLogger();
        silentLogger.setLogLevel(LogLevel.WARN);
        intHttpClient = new IntHttpClient(silentLogger,
                BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create(),
                IntHttpClient.DEFAULT_TIMEOUT, 
                true, 
                ProxyInfo.NO_PROXY_INFO
        );
        detectInfo = new DetectInfoUtility().createDetectInfo();
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
                final URLClassLoader urlClassLoader = URLClassLoader.newInstance(urls);
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
    
    private String[] parseArguments(String[] args) {
        final ListIterator<String> it = Arrays.asList(args).listIterator();
        while (it.hasNext()) {
            String argument = it.next();
            if (argument.contains(BLACKDUCK_OFFLINE_MODE_ARG)) {
                offlineMode = findArgument(it, argument);
            } else if (argument.contains(BLACKDUCK_URL_ARG)) {
                blackduckHost = findArgument(it, argument);
            }
        }
        return args;
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
        final String detectSource = System.getenv(DETECT_SOURCE_SYS_ENV_PROP);
        final String detectLatestReleaseVersion = System.getenv(DETECT_LATEST_RELEASE_VERSION_SYS_ENV_PROP);
        final String detectVersionKey = System.getenv(DETECT_VERSION_KEY_SYS_ENV_PROP);
        final List<String> logMessages = new LinkedList<>();
        final String message = "Self-Update feature is disabled because of the following reasons:";
        addConditionalLogMessageForSysEnvProp(logMessages, DETECT_SOURCE_SYS_ENV_PROP, detectSource);
        addConditionalLogMessageForSysEnvProp(logMessages, DETECT_LATEST_RELEASE_VERSION_SYS_ENV_PROP, detectLatestReleaseVersion);
        addConditionalLogMessageForSysEnvProp(logMessages, DETECT_VERSION_KEY_SYS_ENV_PROP, detectVersionKey);
        if (offlineMode != null && offlineMode.toLowerCase().equals("true")) {
            logMessages.add("The presence of the input argument --"
                    .concat(BLACKDUCK_OFFLINE_MODE_ARG)
                    .concat("=true to run Detect in offline mode is incompatible with the Self-Update feature."));
        }
        if (blackduckHost == null) {
            logMessages.add("The absence of the input argument --"
                    .concat(BLACKDUCK_URL_ARG)
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
    
    private Version convert(String versionString) {
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
    
    private boolean isDownloadVersionTooOld(String downloadVersionString) {
        final Version downloadVersion = convert(downloadVersionString);
        if (downloadVersion.getMajor() < MINIMUM_DETECT_VERSION.getMajor()
            || (downloadVersion.getMajor() == MINIMUM_DETECT_VERSION.getMajor() 
                && (downloadVersion.getMinor() < MINIMUM_DETECT_VERSION.getMinor()
                || (downloadVersion.getMinor() == MINIMUM_DETECT_VERSION.getMinor()
                    && (downloadVersion.getMicro() < MINIMUM_DETECT_VERSION.getMicro()))))) {
            logger.warn("{} The Detect version {} mapped at Black Duck server is "
                    + "not eligible for downgrade as it lacks the self-update feature. "
                    + "The self-update feature is available from {} onwards.", 
                    LOG_PREFIX, downloadVersionString, MINIMUM_DETECT_VERSION);
            return true;
        }
        return false;
    }
    
    private String determineJarDownloadPath() {
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
    
    protected boolean selfUpdate() {
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
                if (!StringUtils.isBlank(newVersionString) 
                        && !newVersionString.equals(currentInstalledVersion.get())
                        && !isDownloadVersionTooOld(newVersionString)) {
                    if (newJarFile.setExecutable(true)) {
                        logger.info("{} Centrally managed version of Detect was downloaded successfully and is ready to be run: {}", LOG_PREFIX, newJarFile.getAbsolutePath());
                        return newJarFile;
                    } else {
                        throw new IntegrationException(String.format("Failed to make %s executable. Please permissions of the parent directory and the file.", newJarFile.getAbsolutePath()));
                    }
                }
            }
        }
        return null;
    }
    
    private boolean isValidDetectFileName(String newFileName) {
        return newFileName.lastIndexOf("-") > -1 && newFileName.lastIndexOf(".") > 0;
    }
    
    private String getVersionFromDetectFileName(String newFileName) {
        return newFileName.replaceAll(".*?((?<!\\w)\\d+([.]\\d+)*).*", "$1");
    }
    
    private HttpUrl buildDownloadUrl() throws IntegrationException {
        final StringBuilder url = new StringBuilder(blackduckHost);
        if (!blackduckHost.endsWith("/")) {
            url.append("/");
        }
        url.append(DOWNLOAD_URL);
        return new HttpUrl(url.toString());
    }

    private Path download(File installDirectory, HttpUrl downloadUrl, String currentVersion) throws IntegrationException, IOException {
        logger.info("{} Checking {} API for centrally managed Detect version to download to {}.", 
                LOG_PREFIX, downloadUrl, installDirectory.getAbsolutePath());
        final Map <String, String> headers = new HashMap<>();
        final Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(DOWNLOAD_VERSION_HEADER, currentVersion);
        final Request request = new Request(downloadUrl, HttpMethod.GET, 
                null, queryParams, headers, null);
        try (final Response response = intHttpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                final String newFileName = response.getHeaderValue(DOWNLOADED_FILE_NAME);
                final Path targetFilePath = Paths.get(installDirectory.getAbsolutePath(), "/", newFileName);
                final File targetFile = targetFilePath.toFile();
                if (!Files.exists(targetFilePath, LinkOption.NOFOLLOW_LINKS)) {
                    logger.debug("{} Writing to file {}.", LOG_PREFIX, targetFilePath.toAbsolutePath());
                    final ReadableByteChannel readableByteChannel = Channels.newChannel(response.getContent());
                    final FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    logger.debug("{} Successfully wrote response to file {}.", LOG_PREFIX, targetFilePath.toAbsolutePath());
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
}
