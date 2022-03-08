package com.synopsys.integration.detectable.detectables.pip.inspector.unit;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.synopsys.integration.common.util.finder.FileFinder;
import com.synopsys.integration.common.util.finder.SimpleFileFinder;
import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.poetry.PoetryDetectable;
import com.synopsys.integration.detectable.detectables.poetry.PoetryExtractor;
import com.synopsys.integration.detectable.detectables.poetry.parser.PoetryLockParser;
import com.synopsys.integration.detectable.detectables.poetry.parser.ToolPoetrySectionParser;

public class PoetryFalsePositiveTest {
    @Test
    public void testApplicableNoFalsePositive() throws URISyntaxException {
        FileFinder fileFinder = Mockito.mock(SimpleFileFinder.class);
        File currentDirectory = new File(System.getProperty("user.dir"));
        File pyprojectToml = new File(getClass().getClassLoader().getResource("detectables/unit/pip/poetry/false_positive_pyproject.toml").toURI());
        Mockito.when(fileFinder.findFile(currentDirectory, "pyproject.toml")).thenReturn(pyprojectToml);
        PoetryDetectable poetryDetectable = new PoetryDetectable(
            new DetectableEnvironment(currentDirectory),
            fileFinder,
            new PoetryExtractor(new PoetryLockParser()),
            new ToolPoetrySectionParser()
        );
        Assertions.assertFalse(poetryDetectable.applicable().getPassed());
    }
}
