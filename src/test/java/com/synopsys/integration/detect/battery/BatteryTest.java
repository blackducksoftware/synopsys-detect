package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.synopsys.integration.detect.Application;
import com.synopsys.integration.detect.configuration.DetectProperty;
import com.synopsys.integration.detectable.detectable.executable.ExecutableOutput;
import com.synopsys.integration.detectable.detectable.executable.ExecutableRunnerException;
import com.synopsys.integration.detectable.detectable.executable.impl.SimpleExecutableRunner;

public final class BatteryTest {
    private final Map<DetectProperty, String> executablesWithSingleResourceFile = new HashMap<>();
    private final Map<DetectProperty, List<String>> executablesWithText = new HashMap<>();
    private final List<String> additionalProperties = new ArrayList<>();
    private final List<String> emptyFileNames = new ArrayList<>();
    private final List<String> resourceFileNames = new ArrayList<>();
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
    private final String name;

    private final AtomicInteger commandCount = new AtomicInteger();
    private final AtomicInteger executableCount = new AtomicInteger();

    public BatteryTest(final String name) {
        this.name = name;
    }

    public void executableFromResourceFile(final DetectProperty detectProperty, final String resourceFile) {
        executablesWithSingleResourceFile.put(detectProperty, "/" + name + "/" + resourceFile);
    }

    public void executable(final DetectProperty detectProperty, final String... responses) {
        executablesWithText.put(detectProperty, Arrays.asList(responses));
    }

    public void sourceFileNamed(final String filename) {
        emptyFileNames.add(filename);
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
        } catch (final ExecutableRunnerException | IOException e) {
            Assertions.assertNull(e, "An exception should not have been thrown!");
        }
    }

    private void runDetect(final List<String> additionalArguments) throws IOException, ExecutableRunnerException {
        final List<String> detectArguments = new ArrayList<>();
        detectArguments.addAll(Arrays.asList("--detect.tools=DETECTOR", "--blackduck.offline.mode=true", "--detect.output.directory=" + outputDirectory, "--detect.bdio.output.path=" + bdioDirectory));

        detectArguments.add("--logging.level.detect=DEBUG");
        detectArguments.add("--detect.source.path=" + sourceDirectory.getCanonicalPath());

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
        testDirectory = new File(batteryDirectory, name);

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

    private List<String> createExecutables() throws IOException {
        final List<String> properties = new ArrayList<>();

        for (final Map.Entry<DetectProperty, String> entry : executablesWithSingleResourceFile.entrySet()) {
            final String command = createCommandTextFileFromResource(entry.getValue());
            final String executable = createExecutableThatTypesFiles(entry.getKey(), command);
            properties.add(executable);
        }

        for (final Map.Entry<DetectProperty, List<String>> entry : executablesWithText.entrySet()) {
            final List<String> commands = new ArrayList<>();
            for (final String s : entry.getValue()) {
                final String commandTextFileFromText = createCommandTextFileFromText(s);
                commands.add(commandTextFileFromText);
            }

            final String executable = createExecutableThatTypesFiles(entry.getKey(), commands);
            properties.add(executable);
        }
        return properties;
    }

    private String createCommandTextFileFromText(final String text) throws IOException {
        final File commandTextFile = new File(mockDirectory, "cmd-" + commandCount.getAndIncrement() + ".txt");
        FileUtils.writeStringToFile(commandTextFile, text, Charset.defaultCharset());
        return commandTextFile.getCanonicalPath();
    }

    private String createCommandTextFileFromResource(final String resource) throws IOException {
        final InputStream commandText = BatteryFiles.asInputStream(resource);
        Assertions.assertNotNull(commandText, "Unable to find resource: " + resource);
        final File commandTextFile = new File(mockDirectory, "cmd-" + commandCount.getAndIncrement() + ".txt");
        Files.copy(commandText, commandTextFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return commandTextFile.getCanonicalPath();
    }

    private String createExecutableThatTypesFiles(final DetectProperty detectProperty, final String... filePaths) throws IOException {
        return createExecutableThatTypesFiles(detectProperty, Arrays.asList(filePaths));
    }

    private String createExecutableThatTypesFiles(final DetectProperty detectProperty, final List<String> filePaths) throws IOException {
        final int id = executableCount.getAndIncrement();
        final File dataFile = new File(mockDirectory, "exe-" + id + ".dat");
        FileUtils.writeStringToFile(dataFile, "0", Charset.defaultCharset());

        String proxyCommand = "@echo off\r\nsetlocal enabledelayedexpansion\r\n";
        proxyCommand += "for /f %%x in (" + dataFile.getCanonicalPath() + ") do (\r\n";
        proxyCommand += "set /a var=%%x\r\n";
        proxyCommand += ")\r\n";
        proxyCommand += "set /a out=%var%+1\r\n";
        proxyCommand += ">" + dataFile.getCanonicalPath() + " echo %out%\r\n";
        int cnt = 0;
        for (final String fileName : filePaths) {
            proxyCommand += "set cmd[" + cnt + "]=\"" + fileName + "\"\r\n";
            cnt++;
        }
        proxyCommand += "type !cmd[%var%]!\r\n";

        final File commandFile = new File(mockDirectory, "exe-" + id + ".bat");
        FileUtils.writeStringToFile(commandFile, proxyCommand, Charset.defaultCharset());

        return "--" + detectProperty.getPropertyKey() + "=" + commandFile.getCanonicalPath();
    }

    private void createFiles() throws IOException {
        for (final String filename : emptyFileNames) {
            final File file = new File(sourceDirectory, filename);
            FileUtils.writeStringToFile(file, "THIS FILE INTENTIONALLY LEFT BLANK", Charset.defaultCharset());
        }

        for (final String resourceFileName : resourceFileNames) {
            final InputStream inputStream = BatteryFiles.asInputStream("/" + name + "/" + resourceFileName);
            final File file = new File(sourceDirectory, resourceFileName);
            Assertions.assertNotNull(inputStream, "Could not find resource file: " + file);
            FileUtils.copyInputStreamToFile(inputStream, file);
        }
    }

    private void assertBdio() throws IOException {
        final File[] bdio = bdioDirectory.listFiles();
        Assertions.assertTrue(bdio != null && bdio.length > 0, "Bdio output files could not be found.");

        if (shouldExpectBdioResources) {
            final File expectedBdioFolder = BatteryFiles.asFile("/" + name + "/bdio");
            final File[] expectedBdioFiles = expectedBdioFolder.listFiles();
            Assertions.assertTrue(expectedBdioFiles != null && expectedBdioFiles.length > 0, "Expected bdio resource files could not be found: " + expectedBdioFolder.getCanonicalPath());
            Assertions.assertEquals(expectedBdioFiles.length, bdio.length, "Detect did not create the expected number of bdio files.");

            final File bdioExpected = expectedBdioFiles[0];
            final File bdioActual = bdio[0];

            Assertions.assertEquals(bdioExpected.getName(), bdioActual.getName(), "Bdio file names were mismatched.");

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
