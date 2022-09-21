package com.synopsys.integration.detectable.functional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.synopsys.integration.bdio.graph.builder.MissingExternalIdException;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.Detectable;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectable.executable.ExecutableFailedException;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.extraction.Extraction;
import com.synopsys.integration.detectable.extraction.ExtractionEnvironment;
import com.synopsys.integration.detectable.factory.DetectableFactory;
import com.synopsys.integration.detectable.util.CycleDetectedException;
import com.synopsys.integration.detectable.util.FunctionalTestFiles;
import com.synopsys.integration.executable.Executable;
import com.synopsys.integration.executable.ExecutableOutput;
import com.synopsys.integration.executable.ExecutableRunnerException;

public abstract class DetectableFunctionalTest {
    @NotNull
    private final String name;

    @NotNull
    private final Path tempDirectory;

    @NotNull
    private final Path sourceDirectory;

    @NotNull
    private final Path outputDirectory;

    @NotNull
    private final FunctionalDetectableExecutableRunner executableRunner;

    @NotNull
    public final DetectableFactory detectableFactory;

    protected DetectableFunctionalTest(@NotNull String name) throws IOException {
        this.name = name;

        this.tempDirectory = Files.createTempDirectory(name);
        this.sourceDirectory = tempDirectory.resolve("source");
        this.outputDirectory = tempDirectory.resolve("output");

        this.executableRunner = new FunctionalDetectableExecutableRunner();

        FileFinder fileFinder = new SimpleFileFinder();
        ExternalIdFactory externalIdFactory = new ExternalIdFactory();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        this.detectableFactory = new DetectableFactory(fileFinder, executableRunner, externalIdFactory, gson);
    }

    @Test
    public void run() throws IOException, DetectableException, ExecutableFailedException, MissingExternalIdException, CycleDetectedException, ExecutableRunnerException,
        ParserConfigurationException, SAXException {
        System.out.println(String.format("Function Test (%s) is using temp directory: %s", name, tempDirectory.toAbsolutePath().toString()));

        setup();

        DetectableEnvironment detectableEnvironment = new DetectableEnvironment(sourceDirectory.toFile());
        Detectable detectable = create(detectableEnvironment);

        DetectableResult applicable = detectable.applicable();
        Assertions.assertTrue(applicable.getPassed(), String.format("Applicable should have passed but was: %s", applicable.toDescription()));

        DetectableResult extractable = detectable.extractable();
        Assertions.assertTrue(extractable.getPassed(), String.format("Extractable should have passed but was: %s", extractable.toDescription()));

        ExtractionEnvironment extractionEnvironment = new ExtractionEnvironment(outputDirectory.toFile());
        Extraction extraction = detectable.extract(extractionEnvironment);

        Assertions.assertNotNull(extraction, "Detectable did not return an extraction!");
        assertExtraction(extraction);

        FileUtils.deleteDirectory(tempDirectory.toFile());
    }

    @NotNull
    public Path addFile(@NotNull String path) throws IOException {
        return addFile(Paths.get(path));
    }

    @NotNull
    public Path addFile(@NotNull Path path) throws IOException {
        return addFile(path, Collections.emptyList());
    }

    @NotNull
    public Path addFile(@NotNull Path path, @NotNull String... lines) throws IOException {
        List<String> fileContent = Arrays.asList(lines);
        return addFile(path, fileContent);
    }

    @NotNull
    public Path addFile(@NotNull Path path, @NotNull Iterable<? extends String> lines) throws IOException {
        Path relativePath = sourceDirectory.resolve(path);
        Files.createDirectories(relativePath.getParent());
        return Files.write(relativePath, lines);
    }

    @NotNull
    public Path addOutputFile(@NotNull String path) throws IOException {
        return addOutputFile(Paths.get(path), Collections.emptyList());
    }

    @NotNull
    public Path addOutputFile(@NotNull Path path, @NotNull Iterable<? extends String> lines) throws IOException {
        Path relativePath = outputDirectory.resolve(path);
        Files.createDirectories(relativePath.getParent());
        return Files.write(relativePath, lines);
    }

    @NotNull
    public Path addOutputFile(@NotNull Path path, String... lines) throws IOException {
        Path relativePath = outputDirectory.resolve(path);
        Files.createDirectories(relativePath.getParent());
        return Files.write(relativePath, Arrays.asList(lines));
    }

    @NotNull
    public Path addFileFromResources(@NotNull Path path, @NotNull String resourcePath) throws IOException {
        List<String> lines = FunctionalTestFiles.asListOfStrings(resourcePath);
        return addFile(path, lines);
    }

    @NotNull
    public Path addDirectory(@NotNull Path path) throws IOException {
        Path relativePath = sourceDirectory.resolve(path);
        return Files.createDirectories(relativePath);
    }

    public void addExecutableOutput(@NotNull ExecutableOutput executableOutput, @NotNull String... command) {
        addExecutableOutput(getSourceDirectory(), executableOutput, command);
    }

    public void addExecutableOutput(@NotNull Path workingDirectory, @NotNull ExecutableOutput executableOutput, @NotNull String... command) {
        addExecutableOutput(workingDirectory, executableOutput, new HashMap<>(), command);
    }

    public void addExecutableOutput(@NotNull ExecutableOutput executableOutput, @NotNull Map<String, String> environment, @NotNull String... command) {
        addExecutableOutput(getSourceDirectory(), executableOutput, environment, command);
    }

    public void addExecutableOutput(
        @NotNull Path workingDirectory,
        @NotNull ExecutableOutput executableOutput,
        @NotNull Map<String, String> environment,
        @NotNull String... command
    ) {
        List<String> commandList = Arrays.asList(command);
        Executable executable = new Executable(workingDirectory.toFile(), environment, commandList);
        executableRunner.addExecutableOutput(executable, executableOutput);
    }

    @NotNull
    public ExecutableOutput createStandardOutput(String... outputLines) {
        String output = String.join(System.lineSeparator(), outputLines);
        return new ExecutableOutput(0, output, "");
    }

    @NotNull
    public ExecutableOutput createStandardOutputFromResource(String resourcePath) {
        String resourceContent = FunctionalTestFiles.asString(resourcePath);
        return new ExecutableOutput(resourceContent, StringUtils.EMPTY);
    }

    @NotNull
    public Path getSourceDirectory() {
        return sourceDirectory;
    }

    @NotNull
    public Path getOutputDirectory() {
        return outputDirectory;
    }

    protected abstract void setup() throws IOException;

    @NotNull
    public abstract Detectable create(@NotNull DetectableEnvironment detectableEnvironment);

    public abstract void assertExtraction(@NotNull Extraction extraction);

    public void assertSuccessfulExtraction(@NotNull Extraction extraction) {
        assertNotEquals(Extraction.ExtractionResultType.EXCEPTION, extraction.getResult(), () -> extraction.getError().getMessage());
        assertEquals(Extraction.ExtractionResultType.SUCCESS, extraction.getResult());
    }
}
