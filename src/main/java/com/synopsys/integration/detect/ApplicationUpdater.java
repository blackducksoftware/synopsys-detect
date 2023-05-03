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

import com.google.common.base.Throwables;

import com.synopsys.integration.blackduck.exception.BlackDuckIntegrationException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationUpdater extends URLClassLoader {
    
    public static final String DOWNLOAD_URL = "api/tools/detect";
    public static final String DOWNLOAD_VERSION_HEADER = "Version";
    public static final String DOWNLOADED_FILE_NAME = "X-Artifactory-Filename";
    public static final String INSTALLED_VERSION_FILE_NAME = "version.txt";
    private static final Version MINIMUM_DETECT_VERSION = new Version(8, 9, 0);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final IntHttpClient intHttpClient;
    private final String[] args;
    private String blackduckHost = null;
    private String offlineMode = null;
    private final DetectInfo detectInfo;
    private final static String BLACKDUCK_OFFLINE_MODE_ARG = "blackduck.offline.mode";
    private final static String BLACKDUCK_URL_ARG = "blackduck.url";
    
    public ApplicationUpdater(String[] args) {
        super(new URL[] {}, Thread.currentThread().getContextClassLoader());
        this.args = parseArguments(args);
        SilentIntLogger silentLogger = new SilentIntLogger();
        silentLogger.setLogLevel(LogLevel.WARN);
        intHttpClient = new IntHttpClient(silentLogger,
                BlackDuckServicesFactory.createDefaultGsonBuilder().setPrettyPrinting().create(),
                IntHttpClient.DEFAULT_TIMEOUT, 
                true, 
                ProxyInfo.NO_PROXY_INFO
        );
        detectInfo = new DetectInfoUtility().createDetectInfo();
    }
    
    private void runMainClass(Path jarPath, String[] launchArgs) throws Exception {
        logger.info("Running new Detect JAR: {}", jarPath);
        try {
            loadJar(jarPath.toAbsolutePath().toString(), launchArgs);
        } catch (InvocationTargetException e) {
            Throwables.propagateIfPossible(e.getTargetException(), Exception.class);
        } finally {
            close();
        }
    }
    
    public void loadJar(final String pathToJar, String[] args) 
            throws IOException, NoSuchMethodException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
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
                    try {
                        final Class<?> loadedClass = urlClassLoader.loadClass(className);
                        classMap.put(loadedClass.getName(), loadedClass);
                    } catch (final ClassNotFoundException ex) {
                        logger.error(ex.getMessage());
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
    }
    
    private boolean checkInstallationDir(Path path) throws IOException {
        Path parentPath = path.getParent();
        Path directoryPath = Files.createDirectories(parentPath);
        if (!Files.exists(directoryPath)) {
            String parentPathString = (parentPath != null) ? parentPath.toString() : null;
            throw new NoSuchFileException(parentPathString, path.toString(), "Unable to locate the installation directory.");
        } else if (Files.exists(path)) {
            if (Files.isWritable(path)) {
                return true;
            } else {
                throw new AccessDeniedException(path.toString(), null, "No write permisison to store Jar in the installation directory: " + path.getFileName());
            }
        }
        return false;
    }
    
    private String[] parseArguments(String[] args) {
        ListIterator<String> it = Arrays.asList(args).listIterator();
        while (it.hasNext()) {
            String argument = it.next();
            if (argument.contains(BLACKDUCK_OFFLINE_MODE_ARG)) {
                offlineMode = findArgument(it, argument).toLowerCase();
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
    
    private boolean canSelfUpdate() {
        String detectSource = System.getenv("DETECT_SOURCE");
        String detectLatestReleaseVersion = System.getenv("DETECT_LATEST_RELEASE_VERSION");
        String detectVersionKey = System.getenv("DETECT_VERSION_KEY");
        return detectSource == null 
                && detectLatestReleaseVersion == null
                && detectVersionKey == null
                && offlineMode == null || offlineMode.equals("false")
                && blackduckHost != null;
    }
    
    private Version convert(String versionString) {
        List<Integer> versionParts = Arrays.stream(versionString.split("\\."))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        Version versionObj;
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
        Version downloadVersion = convert(downloadVersionString);
        if (downloadVersion.getMajor() < MINIMUM_DETECT_VERSION.getMajor()
            || (downloadVersion.getMajor() == MINIMUM_DETECT_VERSION.getMajor() 
                && (downloadVersion.getMinor() < MINIMUM_DETECT_VERSION.getMinor()
                || (downloadVersion.getMinor() == MINIMUM_DETECT_VERSION.getMinor()
                    && (downloadVersion.getMicro() < MINIMUM_DETECT_VERSION.getMicro()))))) {
            logger.warn("The Detect version mapped at Black Duck server is "
                    + "not eligible for downgrade as it lacks the self-update feature. "
                    + "The self-update feature is available from 8.9.0 onwards.");
            return true;
        }
        return false;
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
                File newDetectJar = installOrUpdateScanner(jarDownloadPath);
                if (newDetectJar != null) {
                    runMainClass(newDetectJar.toPath(), args);
                    return true;
                }
            } catch (BlackDuckIntegrationException ex) {
                logger.error(ex.getMessage());
                return true;
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                return true;
            }
        }
        return false;
    }

    private File installOrUpdateScanner(String dirPath) throws BlackDuckIntegrationException {
        try {
            File installDirectory = new File(dirPath);
            if (!installDirectory.exists()) {
                installDirectory.mkdir();
            } else if (!checkInstallationDir(installDirectory.toPath())) {
                return null;
            }
            HttpUrl downloadUrl = buildDownloadUrl();
            Optional<String> currentInstalledVersion = determineInstalledVersion();
            Path newJar = download(installDirectory, downloadUrl, currentInstalledVersion.orElse(""));
            if (newJar != null) {
                File newJarFile = newJar.toFile();
                String newFileName = newJar.getFileName().toString();
                if (isValidDetectFileName(newFileName)) {
                    String newVersionString = getVersionFromDetectFileName(newFileName);
                    if (!newVersionString.isBlank() 
                            && !newVersionString.equals(currentInstalledVersion.get())
                            && !isDownloadVersionTooOld(newVersionString)) {
                        if (newJarFile.setExecutable(true)) {
                            logger.info("Detect was downloaded/found successfully: " + newJarFile.getAbsolutePath());
                            return newJarFile;
                        } else {
                            throw new IntegrationException(String.format("Attempt to make %s executable failed.", newJarFile.getAbsolutePath()));
                        }
                    }
                }
            }
        } catch (IntegrationException | IOException e) {
            throw new BlackDuckIntegrationException("Detect could not be downloaded successfully: " + e.getMessage(), e);
        }
        return null;
    }
    
    private boolean isValidDetectFileName(String newFileName) {
        return newFileName.lastIndexOf("-") > -1 && newFileName.lastIndexOf(".") > 0;
    }
    
    private String getVersionFromDetectFileName(String newFileName) {
        return newFileName.substring(newFileName.lastIndexOf("-") + 1, newFileName.lastIndexOf("."));
    }
    
    private HttpUrl buildDownloadUrl() throws IntegrationException {
        StringBuilder url = new StringBuilder(blackduckHost);
        if (!blackduckHost.endsWith("/")) {
            url.append("/");
        }
        url.append(DOWNLOAD_URL);
        return new HttpUrl(url.toString());
    }

    private Path download(File installDirectory, HttpUrl downloadUrl, String currentVersion) throws IntegrationException, IOException {
        logger.debug(String.format("Downloading artifact to '%s' from '%s'.", installDirectory.getAbsolutePath(), downloadUrl));
        Map <String, String> headers = new HashMap<>();
        Map<String, Set<String>> queryParams = new HashMap<>();
        headers.put(DOWNLOAD_VERSION_HEADER, currentVersion);
        Request request = new Request(downloadUrl, HttpMethod.GET, null, queryParams, headers, null);
        try (Response response = intHttpClient.execute(request)) {
            if (response.isStatusCodeSuccess()) {
                String newFileName = response.getHeaderValue(DOWNLOADED_FILE_NAME);
                Path targetFilePath = Paths.get(installDirectory.getAbsolutePath(), "/", newFileName);
                File targetFile = targetFilePath.toFile();
                if (!Files.exists(targetFilePath, LinkOption.NOFOLLOW_LINKS)) {
                    logger.debug("Writing to file.");
                    ReadableByteChannel readableByteChannel = Channels.newChannel(response.getContent());
                    FileOutputStream fileOutputStream = new FileOutputStream(targetFile);
                    fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
                    logger.debug("Successfully wrote response to file.");
                }
                return targetFilePath;
            } else if (response.getStatusCode() == 304) {
                logger.debug("Present Detect installation is up to date - skipping download.");
            } else {
                logger.trace("Unable to download artifact. Response code: " + response.getStatusCode() + " " + response.getStatusMessage());
            }
        }
        return null;
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
