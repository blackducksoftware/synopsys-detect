package com.synopsys.integration.detect.util;

import static org.junit.jupiter.api.condition.OS.WINDOWS;

import java.nio.file.Path;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.condition.DisabledOnOs;

import com.synopsys.integration.detect.type.OperatingSystemType;

public class TildeInPathResolverTest {
    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTilde() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX, true);
        final Path resolved = resolver.resolveTilde("~/Documents/source/funtional/detect");

        Assertions.assertEquals("/Users/ekerwin/Documents/source/funtional/detect", resolved.toString(), "Tilde's should be resolved on Unix operating systems.");
    }

    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTildeInWindows() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);

        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.WINDOWS, true);
        final String filePath = "~/Documents/source/funtional/detect";
        final Path resolved = resolver.resolveTilde(filePath);

        Assertions.assertEquals(filePath, resolved.toString(), "Tilde resolution should not occur on Windows.");
    }

    @Test
    @DisabledOnOs(WINDOWS) // Due to backslashes being flipped.
    public void testResolvingTildeInTheMiddleOfAPath() {
        Assume.assumeFalse(SystemUtils.IS_OS_WINDOWS);
        
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX, true);
        final String filePath = "/Documents/~source/~/funtional/detect";
        final Path resolved = resolver.resolveTilde(filePath);

        Assertions.assertEquals(filePath, resolved.toString(), "Tilde's in the middle of the path should not be resolved.");
    }
}
