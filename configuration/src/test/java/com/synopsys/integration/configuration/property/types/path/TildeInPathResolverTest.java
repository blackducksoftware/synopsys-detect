package com.synopsys.integration.configuration.property.types.path;

import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;

public class TildeInPathResolverTest {
    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTilde() {
        TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Path resolved = resolver.resolvePath("~/Documents/source/functional/detect");

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals("/Users/ekerwin/Documents/source/functional/detect", resolved.toString(), "Tilde's should be resolved on Unix operating systems.");
    }

    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTildeInTheMiddleOfAPath() {
        TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        final String filePath = "/Documents/~source/~/functional/detect";
        Path resolved = resolver.resolvePath(filePath);

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals(filePath, resolved.toString(), "Tilde's in the middle of the path should not be resolved.");
    }

    @Test
    public void testBlankPath() {
        TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Assertions.assertEquals("", resolver.resolvePath("").toString());
    }

    @Test
    @EnabledOnOs(WINDOWS) // Path is more forgiving of whitespace on Unix systems.
    public void testWhitespacePath() {
        TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Assertions.assertThrows(InvalidPathException.class, () -> resolver.resolvePath("  "));
    }
}
