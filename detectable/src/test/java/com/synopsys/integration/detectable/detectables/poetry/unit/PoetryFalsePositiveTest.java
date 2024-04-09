package com.synopsys.integration.detectable.detectables.poetry.unit;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectable.result.DetectableResult;
import com.synopsys.integration.detectable.detectable.result.FileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.FilesNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.PoetryLockfileNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectable.result.SectionNotFoundDetectableResult;
import com.synopsys.integration.detectable.detectables.poetry.PoetryDetectable;
import com.synopsys.integration.detectable.detectables.poetry.PoetryExtractor;
import com.synopsys.integration.detectable.detectables.poetry.PoetryOptions;
import com.synopsys.integration.detectable.detectables.poetry.parser.PoetryLockParser;
import com.synopsys.integration.detectable.detectables.poetry.parser.ToolPoetrySectionParser;
import com.synopsys.integration.detectable.detectables.poetry.parser.ToolPoetrySectionResult;

public class PoetryFalsePositiveTest {
    @Test
    public void testApplicable_missingToolPoetrySection() throws URISyntaxException {
        FileFinder fileFinder = Mockito.mock(SimpleFileFinder.class);
        File currentDirectory = new File(System.getProperty("user.dir"));
        File pyprojectToml = new File(getClass().getClassLoader().getResource("detectables/unit/pip/poetry/false_positive_pyproject.toml").toURI());
        Mockito.when(fileFinder.findFile(currentDirectory, "pyproject.toml")).thenReturn(pyprojectToml);
        PoetryDetectable poetryDetectable = new PoetryDetectable(
            new DetectableEnvironment(currentDirectory),
            fileFinder,
            new PoetryExtractor(new PoetryLockParser()),
            new ToolPoetrySectionParser(),
            new PoetryOptions(Collections.emptyList())
        );

        DetectableResult result = poetryDetectable.applicable();

        Assertions.assertTrue(result instanceof SectionNotFoundDetectableResult);
        Assertions.assertFalse(result.getPassed());
    }

    @Test
    public void testApplicable_missingFiles() {
        FileFinder fileFinder = Mockito.mock(SimpleFileFinder.class);
        File currentDirectory = new File(System.getProperty("user.dir"));
        PoetryDetectable poetryDetectable = new PoetryDetectable(
            new DetectableEnvironment(currentDirectory),
            fileFinder,
            new PoetryExtractor(new PoetryLockParser()),
            new ToolPoetrySectionParser(),
            new PoetryOptions(Collections.emptyList())
        );

        DetectableResult result = poetryDetectable.applicable();

        Assertions.assertTrue(result instanceof FilesNotFoundDetectableResult);
        Assertions.assertFalse(result.getPassed());
    }

    @Test
    public void testExtractable_lockFileNotFound() {
        FileFinder fileFinder = Mockito.mock(SimpleFileFinder.class);
        File currentDirectory = new File(System.getProperty("user.dir"));
        File pyprojectToml = Mockito.mock(File.class);
        Mockito.when(fileFinder.findFile(currentDirectory, "pyproject.toml")).thenReturn(pyprojectToml);

        ToolPoetrySectionParser sectionParser = Mockito.mock(ToolPoetrySectionParser.class);
        Mockito.when(sectionParser.parseToolPoetrySection(pyprojectToml)).thenReturn(Mockito.mock(ToolPoetrySectionResult.class));

        PoetryDetectable poetryDetectable = new PoetryDetectable(
            new DetectableEnvironment(currentDirectory),
            fileFinder,
            new PoetryExtractor(new PoetryLockParser()),
            sectionParser,
            new PoetryOptions(Collections.emptyList())
        );

        poetryDetectable.applicable(); // attempt to find applicable files

        DetectableResult result  = poetryDetectable.extractable();

        Assertions.assertTrue(result instanceof PoetryLockfileNotFoundDetectableResult);
        Assertions.assertFalse(result.getPassed());
    }

    @Test
    public void testExtractable_nonEmptyExcludedGroupsAndPyprojectTomlNotFound() {
        FileFinder fileFinder = Mockito.mock(SimpleFileFinder.class);
        File currentDirectory = new File(System.getProperty("user.dir"));
        File poetryLock = Mockito.mock(File.class);
        Mockito.when(fileFinder.findFile(currentDirectory, "Poetry.lock")).thenReturn(poetryLock);

        ToolPoetrySectionParser sectionParser = Mockito.mock(ToolPoetrySectionParser.class);
        Mockito.when(sectionParser.parseToolPoetrySection(null)).thenReturn(Mockito.mock(ToolPoetrySectionResult.class));

        PoetryDetectable poetryDetectable = new PoetryDetectable(
            new DetectableEnvironment(currentDirectory),
            fileFinder,
            new PoetryExtractor(new PoetryLockParser()),
            sectionParser,
            new PoetryOptions(Collections.singletonList("dev")) // tell PoetryDetectable that we want to filter out dev dependencies
        );

        poetryDetectable.applicable(); // attempt to find applicable files

        DetectableResult result  = poetryDetectable.extractable();

        Assertions.assertTrue(result instanceof FileNotFoundDetectableResult);
        Assertions.assertFalse(result.getPassed());
    }
}
