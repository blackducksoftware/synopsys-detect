package com.synopsys.integration.detect.battery.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import com.synopsys.integration.configuration.property.Property;
import com.synopsys.integration.detect.configuration.DetectProperties;

import freemarker.template.TemplateException;

public class BatteryContext {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String ENVIRONMENT_VARIABLE_BATTERY_TESTS_PATH = "BATTERY_TESTS_PATH";

    private final List<BatteryExecutable> executables = new ArrayList<>();

    private final List<String> emptyFileNames = new ArrayList<>();
    private final List<String> resourceFileNames = new ArrayList<>();
    private final List<String> resourceZipNames = new ArrayList<>();
    private String resourceZipIntoSource = null;
    private String sourceDirectoryName = "source";

    private File batteryDirectory;
    private File mockDirectory;
    private File outputDirectory;

    private File bdioDirectory;
    private File sourceDirectory;

    private final AtomicInteger commandCount = new AtomicInteger();
    private final AtomicInteger executableCount = new AtomicInteger();

    private final String testName;
    private final String resourcePrefix;

    public BatteryContext(final String testName) {
        this.testName = testName;
        this.resourcePrefix = testName;
    }

    public BatteryContext(final String testName, final String resourcePrefix) {
        this.testName = testName;
        this.resourcePrefix = resourcePrefix;
    }

    public File getOutputDirectory() {
        return outputDirectory;
    }

    public File getBdioDirectory() {
        return bdioDirectory;
    }

    public File getScriptDirectory() {
        return batteryDirectory;
    }

    public String getTestName() {
        return testName;
    }

    public String getResourcePrefix() {
        return resourcePrefix;
    }

    public void checkAndCleanupBatteryDirectory() {
        if (StringUtils.isBlank(System.getenv().get(ENVIRONMENT_VARIABLE_BATTERY_TESTS_PATH))) {
            logger.info("The environment variable {} not set. Cleaning up battery directory.", ENVIRONMENT_VARIABLE_BATTERY_TESTS_PATH);
            FileUtils.deleteQuietly(batteryDirectory);
        }
    }

    public List<String> initialize() throws IOException, TemplateException {
        checkEnvironment();
        initializeDirectories();
        createFiles();
        return createExecutables();
    }

    private List<String> prefixResources(String... resourceFiles) {
        return Arrays.stream(resourceFiles)
                   .map(it -> "/" + this.resourcePrefix + "/" + it)
                   .collect(Collectors.toList());
    }

    private void initializeDirectories() throws IOException {
        File testDirectory = new File(batteryDirectory, testName);

        if (testDirectory.exists()) {
            FileUtils.deleteDirectory(testDirectory);
        }

        mockDirectory = new File(testDirectory, "mock");
        outputDirectory = new File(testDirectory, "output");
        bdioDirectory = new File(testDirectory, "bdio");
        //ah ha
        sourceDirectory = new File(testDirectory, sourceDirectoryName);

        Assertions.assertTrue(outputDirectory.mkdirs());
        Assertions.assertTrue(sourceDirectory.mkdirs());
        Assertions.assertTrue(bdioDirectory.mkdirs());
        Assertions.assertTrue(mockDirectory.mkdirs());
    }

    private void checkEnvironment() {
        if (StringUtils.isNotBlank(System.getenv().get(ENVIRONMENT_VARIABLE_BATTERY_TESTS_PATH))) {
            logger.info("The environment variable BATTERY_TESTS_PATH is set.");
            batteryDirectory = new File(System.getenv(ENVIRONMENT_VARIABLE_BATTERY_TESTS_PATH));
        } else {
            try {
                batteryDirectory = Files.createTempDirectory("detect_battery").toFile();
            } catch (IOException ex) {
                logger.error("Error initializing battery directory.", ex);
            }
        }
        logger.info("Battery test directory: {}", batteryDirectory.getAbsolutePath());
        if (!batteryDirectory.exists()) {
            Assertions.assertTrue(batteryDirectory.mkdirs(), String.format("Failed to create battery directory at: %s", batteryDirectory.getAbsolutePath()));
        }
        Assertions.assertTrue(batteryDirectory.exists(), "The detect battery path must exist.");
    }

    private List<String> createExecutables() throws IOException, TemplateException {
        List<String> properties = new ArrayList<>();

        for (BatteryExecutable executable : executables) {
            int id = executableCount.getAndIncrement();
            Assertions.assertNotNull(executable.creator, "Every battery executable must have a 'creator' or a way to actually generate the executable..");
            BatteryExecutableInfo info = new BatteryExecutableInfo(mockDirectory, sourceDirectory);
            File commandFile = executable.creator.createExecutable(id, info, commandCount);
            if (executable.detectProperty != null) {
                properties.add("--" + executable.detectProperty.getKey() + "=" + commandFile.getCanonicalPath());
            } else if (executable.linuxSourceFileName != null && executable.windowsSourceFileName != null) {
                File target;
                if (SystemUtils.IS_OS_WINDOWS) {
                    target = new File(sourceDirectory, executable.windowsSourceFileName);
                } else {
                    target = new File(sourceDirectory, executable.linuxSourceFileName);
                }
                FileUtils.moveFile(commandFile, target);
            } else {
                throw new RuntimeException("Every battery executable must either specify an override property or a location (for both linux and windows) in the source directory for the executable to go.");
            }
        }
        return properties;
    }

    private void createFiles() throws IOException {
        for (String filename : emptyFileNames) {
            File file = new File(sourceDirectory, filename);
            FileUtils.writeStringToFile(file, "THIS FILE INTENTIONALLY LEFT BLANK", Charset.defaultCharset());
        }

        for (String resourceFileName : resourceFileNames) {
            InputStream inputStream = BatteryFiles.asInputStream("/" + resourcePrefix + "/" + resourceFileName);
            File file = new File(sourceDirectory, resourceFileName);
            Assertions.assertNotNull(inputStream, "Could not find resource file: " + file);
            FileUtils.copyInputStreamToFile(inputStream, file);
        }

        for (String resourceZipFileName : resourceZipNames) {
            File zipFile = BatteryFiles.asFile("/" + resourcePrefix + "/" + resourceZipFileName + ".zip");
            File target = new File(sourceDirectory, resourceZipFileName);
            ZipUtil.unpack(zipFile, target);
        }

        if (resourceZipIntoSource != null) {
            File zipFile = BatteryFiles.asFile("/" + resourcePrefix + "/" + resourceZipIntoSource + ".zip");
            ZipUtil.unpack(zipFile, sourceDirectory);
        }
    }

    public void sourceDirectoryNamed(String sourceDirectoryName) {
        this.sourceDirectoryName = sourceDirectoryName;
    }

    /**
     * NOTE: The order in which you provide the names of executable output resource files must match the order in which their corresponding commands are invoked at runtime.
     * ex) The GoModCliExtractor invokes the command 'go list' before the command 'go version', so go-list.xout must be ordered before go-version.xout in resourceFiles when constructing
     * a battery test for the go mod detectable.
     */
    public void executableFromResourceFiles(Property detectProperty, String... resourceFiles) {
        ResourceTypingExecutableCreator creator = new ResourceTypingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.propertyOverrideExecutable(detectProperty, creator));
    }

    public void executableSourceFileFromResourceFiles(String windowsName, String linuxName, String... resourceFiles) {
        ResourceTypingExecutableCreator creator = new ResourceTypingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.sourceFileExecutable(windowsName, linuxName, creator));
    }

    public ResourceCopyingExecutableCreator executableThatCopiesFiles(Property detectProperty, String... resourceFiles) {
        ResourceCopyingExecutableCreator resourceCopyingExecutable = new ResourceCopyingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.propertyOverrideExecutable(detectProperty, resourceCopyingExecutable));
        return resourceCopyingExecutable;
    }

    public ResourceCopyingExecutableCreator executableSourceFileThatCopiesFiles(String windowsName, String linuxName, String... resourceFiles) {
        ResourceCopyingExecutableCreator resourceCopyingExecutable = new ResourceCopyingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.sourceFileExecutable(windowsName, linuxName, resourceCopyingExecutable));
        return resourceCopyingExecutable;
    }

    public void executable(Property detectProperty, String... responses) {
        executables.add(BatteryExecutable.propertyOverrideExecutable(detectProperty, new StringTypingExecutableCreator(Arrays.asList(responses))));
    }

    public void git(String origin, String branch) {
        sourceFileNamed(".git");
        executable(DetectProperties.DETECT_GIT_PATH.getProperty(), origin, branch);
    }

    public void sourceFileNamed(String filename) {
        emptyFileNames.add(filename);
    }

    public void addDirectlyToSourceFolderFromExpandedResource(String filename) {
        resourceZipIntoSource = filename;
    }

    public void sourceFolderFromExpandedResource(String filename) {
        resourceZipNames.add(filename);
    }

    public void sourceFileFromResource(String filename) {
        resourceFileNames.add(filename);
    }

    public File getSourceDirectory() {
        return sourceDirectory;
    }
}
