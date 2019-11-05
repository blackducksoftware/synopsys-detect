package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zeroturnaround.zip.ZipUtil;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;

import freemarker.template.TemplateException;

public final class BatteryTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final List<BatteryExecutable> executables = new ArrayList<>();

    private final List<String> additionalProperties = new ArrayList<>();
    private final List<String> emptyFileNames = new ArrayList<>();
    private final List<String> resourceFileNames = new ArrayList<>();
    private final List<String> resourceZipNames = new ArrayList<>();
    private final Map<String, Integer> bdioSizes = new HashMap<>();
    private boolean shouldExpectBdioResources = false;
    private String sourceDirectoryName = "source";

    private File batteryDirectory;
    private File testDirectory;
    private File mockDirectory;
    private File outputDirectory;
    private File bdioDirectory;
    private File sourceDirectory;

    private final Gson gson = new Gson();
    private final String testName;
    private final String resourcePrefix;

    private final AtomicInteger commandCount = new AtomicInteger();
    private final AtomicInteger executableCount = new AtomicInteger();

    public BatteryTest(final String name) {
        this.testName = name;
        this.resourcePrefix = name;
    }

    public BatteryTest(final String testName, final String resourcePrefix) {
        this.testName = testName;
        this.resourcePrefix = resourcePrefix;
    }

    private List<String> prefixResources(final String... resourceFiles) {
        return Arrays.stream(resourceFiles)
                   .map(it -> "/" + this.resourcePrefix + "/" + it)
                   .collect(Collectors.toList());
    }

    public void executableFromResourceFiles(final DetectProperty detectProperty, final String... resourceFiles) {
        final ResourceTypingExecutableCreator creator = new ResourceTypingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.propertyOverrideExecutable(detectProperty, creator));
    }

    public void executableSourceFileFromResourceFiles(final String windowsName, final String linuxName, final String... resourceFiles) {
        final ResourceTypingExecutableCreator creator = new ResourceTypingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.sourceFileExecutable(windowsName, linuxName, creator));
    }

    public ResourceCopyingExecutableCreator executableThatCopiesFiles(final DetectProperty detectProperty, final String... resourceFiles) {
        final ResourceCopyingExecutableCreator resourceCopyingExecutable = new ResourceCopyingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.propertyOverrideExecutable(detectProperty, resourceCopyingExecutable));
        return resourceCopyingExecutable;
    }

    public ResourceCopyingExecutableCreator executableSourceFileThatCopiesFiles(final String windowsName, final String linuxName, final String... resourceFiles) {
        final ResourceCopyingExecutableCreator resourceCopyingExecutable = new ResourceCopyingExecutableCreator(prefixResources(resourceFiles));
        executables.add(BatteryExecutable.sourceFileExecutable(windowsName, linuxName, resourceCopyingExecutable));
        return resourceCopyingExecutable;
    }

    public void executable(final DetectProperty detectProperty, final String... responses) {
        executables.add(BatteryExecutable.propertyOverrideExecutable(detectProperty, new StringTypingExecutableCreator(Arrays.asList(responses))));
    }

    public void git(final String origin, final String branch) {
        sourceFileNamed(".git");
        executable(DetectProperty.DETECT_GIT_PATH, origin, branch);
    }

    public void sourceFileNamed(final String filename) {
        emptyFileNames.add(filename);
    }

    public void sourceFolderFromExpandedResource(final String filename) {
        resourceZipNames.add(filename);
    }

    public void sourceFileFromResource(final String filename) {
        resourceFileNames.add(filename);
    }

    public void expectBdioFile(final String named, final int size) {
        bdioSizes.put(named, size);
    }

    public void expectBdioResources() {
        shouldExpectBdioResources = true;
    }

    public void property(final DetectProperty property, final String value) {
        property(property.getPropertyKey(), value);
    }

    public void property(final String property, final String value) {
        additionalProperties.add("--" + property + "=" + value);
    }

    public void run() {
        try {
            checkEnvironment();
            initializeDirectories();
            createFiles();
            final List<String> executableArguments = createExecutables();
            runDetect(executableArguments);

            assertBdio();
        } catch (final ExecutableRunnerException | IOException | JSONException | TemplateException | BdioCompare.BdioCompareException e) {
            Assertions.assertNull(e, "An exception should not have been thrown!");
        }
    }

    private void runDetect(final List<String> additionalArguments) throws IOException, ExecutableRunnerException {
        final List<String> detectArguments = new ArrayList<>();
        final Map<DetectProperty, String> properties = new HashMap<>();
        
        properties.put(DetectProperty.DETECT_TOOLS, "DETECTOR");
        properties.put(DetectProperty.BLACKDUCK_OFFLINE_MODE, "true");
        properties.put(DetectProperty.DETECT_OUTPUT_PATH, outputDirectory.getCanonicalPath());
        properties.put(DetectProperty.DETECT_BDIO_OUTPUT_PATH, bdioDirectory.getCanonicalPath());
        properties.put(DetectProperty.DETECT_CLEANUP, "false");
        properties.put(DetectProperty.LOGGING_LEVEL_COM_SYNOPSYS_INTEGRATION, "DEBUG");
        properties.put(DetectProperty.DETECT_SOURCE_PATH, sourceDirectory.getCanonicalPath());
        for (final Map.Entry<DetectProperty, String> entry : properties.entrySet()) {
            detectArguments.add("--" + entry.getKey().getPropertyKey() + "=" + entry.getValue());
        }

        detectArguments.addAll(additionalArguments);
        detectArguments.addAll(additionalProperties);

        if (!executeDetectJar(detectArguments)) {
            executeDetectStatic(detectArguments);
        }
    }

    private void executeDetectStatic(final List<String> detectArguments) {
        final boolean previous = Application.SHOULD_EXIT;
        Application.SHOULD_EXIT = false;
        Application.main(detectArguments.toArray(new String[0]));
        Application.SHOULD_EXIT = previous;
    }

    private boolean executeDetectJar(final List<String> detectArguments) throws ExecutableRunnerException {
        final String java = System.getenv("BATTERY_TESTS_JAVA_PATH");
        final String detectJar = System.getenv("BATTERY_TESTS_DETECT_JAR_PATH");
        final boolean bothExist = StringUtils.isNotBlank(java) && StringUtils.isNotBlank(detectJar) && new File(java).exists() && new File(detectJar).exists();
        if (!bothExist) {
            return false;
        }

        final List<String> javaArguments = new ArrayList<>();
        javaArguments.add("-jar");
        javaArguments.add(detectJar);
        javaArguments.addAll(detectArguments);

        final SimpleExecutableRunner executableRunner = new SimpleExecutableRunner();
        final ExecutableOutput result = executableRunner.execute(outputDirectory, java, javaArguments);

        Assertions.assertEquals(0, result.getReturnCode(), "Detect returned a non-zero exit code:" + result.getReturnCode());

        final List<String> lines = result.getStandardOutputAsList();

        Assertions.assertTrue(lines.size() > 0, "Detect wrote nothing to standard out.");

        return true;
    }

    private void initializeDirectories() throws IOException {
        testDirectory = new File(batteryDirectory, testName);

        if (testDirectory.exists()) {
            FileUtils.deleteDirectory(testDirectory);
        }

        mockDirectory = new File(testDirectory, "mock");
        outputDirectory = new File(testDirectory, "output");
        bdioDirectory = new File(testDirectory, "bdio");
        sourceDirectory = new File(testDirectory, sourceDirectoryName);

        outputDirectory.mkdirs();
        sourceDirectory.mkdirs();
        bdioDirectory.mkdirs();
        mockDirectory.mkdirs();
    }

    private void checkEnvironment() {
        Assumptions.assumeTrue(StringUtils.isNotBlank(System.getenv().get("BATTERY_TESTS_PATH")));

        batteryDirectory = new File(System.getenv("BATTERY_TESTS_PATH"));
        Assertions.assertTrue(batteryDirectory.exists(), "The detect battery path must exist.");
    }

    private List<String> createExecutables() throws IOException, TemplateException {
        final List<String> properties = new ArrayList<>();

        for (final BatteryExecutable executable : executables) {
            final int id = executableCount.getAndIncrement();
            Assertions.assertNotNull(executable.creator, "Every battery executable must have a 'creator' or a way to actually generate the executable..");
            final BatteryExecutableInfo info = new BatteryExecutableInfo(mockDirectory, sourceDirectory);
            final File commandFile = executable.creator.createExecutable(id, info, commandCount);
            if (executable.detectProperty != null) {
                properties.add("--" + executable.detectProperty.getPropertyKey() + "=" + commandFile.getCanonicalPath());
            } else if (executable.linuxSourceFileName != null && executable.windowsSourceFileName != null) {
                final File target;
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
        for (final String filename : emptyFileNames) {
            final File file = new File(sourceDirectory, filename);
            FileUtils.writeStringToFile(file, "THIS FILE INTENTIONALLY LEFT BLANK", Charset.defaultCharset());
        }

        for (final String resourceFileName : resourceFileNames) {
            final InputStream inputStream = BatteryFiles.asInputStream("/" + resourcePrefix + "/" + resourceFileName);
            final File file = new File(sourceDirectory, resourceFileName);
            Assertions.assertNotNull(inputStream, "Could not find resource file: " + file);
            FileUtils.copyInputStreamToFile(inputStream, file);
        }

        for (final String resourceZipFileName : resourceZipNames) {
            final File zipFile = BatteryFiles.asFile("/" + resourcePrefix + "/" + resourceZipFileName + ".zip");
            final File target = new File(sourceDirectory, resourceZipFileName);
            ZipUtil.unpack(zipFile, target);
        }
    }

    private void assertBdio() throws IOException, JSONException, BdioCompare.BdioCompareException {
        final File[] bdio = bdioDirectory.listFiles();
        Assertions.assertTrue(bdio != null && bdio.length > 0, "Bdio output files could not be found.");

        if (shouldExpectBdioResources) {
            final File expectedBdioFolder = BatteryFiles.asFile("/" + resourcePrefix + "/bdio");
            final File[] expectedBdioFiles = expectedBdioFolder.listFiles();
            Assertions.assertTrue(expectedBdioFiles != null && expectedBdioFiles.length > 0, "Expected bdio resource files could not be found: " + expectedBdioFolder.getCanonicalPath());
            Assertions.assertEquals(expectedBdioFiles.length, bdio.length, "Detect did not create the expected number of bdio files.");

            final List<File> actualByName = Arrays.stream(bdio).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());
            final List<File> expectedByName = Arrays.stream(expectedBdioFiles).sorted(Comparator.comparing(File::getName)).collect(Collectors.toList());

            for (int i = 0; i < expectedByName.size(); i++) {
                final File expected = expectedByName.get(i);
                final File actual = actualByName.get(i);
                Assertions.assertEquals(expected.getName(), actual.getName(), "Bdio file names did not match when sorted.");

                final String expectedJson = FileUtils.readFileToString(expected, Charset.defaultCharset());
                final String actualJson = FileUtils.readFileToString(actual, Charset.defaultCharset());

                final JSONArray expectedJsonArray = (JSONArray) JSONParser.parseJSON(expectedJson);
                final JSONArray actualJsonArray = (JSONArray) JSONParser.parseJSON(actualJson);

                BdioCompare compare = new BdioCompare();
                List<BdioCompare.BdioIssue> issues = compare.compare(expectedJsonArray, actualJsonArray);

                if (issues.size() > 0) {
                    logger.error("=================");
                    logger.error("BDIO Issues");
                    logger.error("Expected: " + expected.getCanonicalPath());
                    logger.error("Actual: " + actual.getCanonicalPath());
                    logger.error("=================");
                    issues.forEach(issue -> logger.error(issue.getIssue()));
                    logger.error("=================");
                }
                Assertions.assertEquals(0, issues.size(), "The BDIO comparison failed, one or more issues were found, please check the logs.");
            }

        } else {
            Assertions.assertEquals(bdio.length, bdioSizes.keySet().size(), "Detect did not create the expected number of bdio files.");
            for (final String key : bdioSizes.keySet()) {
                File file = null;
                for (final File bdioFile : bdio) {
                    if (bdioFile.getName().equals(key)) {
                        file = bdioFile;
                    }
                }
                Assertions.assertNotNull(file, "Could not find bdio file.");
                final JsonArray json = gson.fromJson(FileUtils.readFileToString(file, Charset.defaultCharset()), JsonArray.class);
                Assertions.assertEquals((int) bdioSizes.get(key), json.size(), "A bdio file did not have the required number of components.");
            }
        }
    }

    public void sourceDirectoryNamed(final String sourceDirectoryName) {
        this.sourceDirectoryName = sourceDirectoryName;
    }
}
