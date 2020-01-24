package com.synopsys.integration.detect.util;

import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.nio.file.InvalidPathException;
import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.condition.DisabledOnOs;

public class TildeInPathResolverTest {
    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTilde() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        final Path resolved = resolver.resolvePath("~/Documents/source/funtional/detect");

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals("/Users/ekerwin/Documents/source/funtional/detect", resolved.toString(), "Tilde's should be resolved on Unix operating systems.");
    }

    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTildeInWindows() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        final String filePath = "~/Documents/source/funtional/detect";
        final Path resolved = resolver.resolvePath(filePath);

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals(filePath, resolved.toString(), "Tilde resolution should not occur on Windows.");
    }

    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTildeInTheMiddleOfAPath() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        final String filePath = "/Documents/~source/~/funtional/detect";
        final Path resolved = resolver.resolvePath(filePath);

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals(filePath, resolved.toString(), "Tilde's in the middle of the path should not be resolved.");
    }

    @Test
    public void testBlankPath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Assertions.assertEquals("", resolver.resolvePath("").toString());
    }

    @Test
    public void testWhitespacePath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin");
        Assertions.assertThrows(InvalidPathException.class, () -> {
            resolver.resolvePath("  ");
        });
    }
}
