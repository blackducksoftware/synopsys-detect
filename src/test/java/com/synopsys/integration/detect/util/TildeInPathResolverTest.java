package com.synopsys.integration.detect.util;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import com.synopsys.integration.detect.type.OperatingSystemType;

public class TildeInPathResolverTest {
    @Test
    public void testResolvingTilde() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX, true);
        final Path resolved = resolver.resolvePath("~/Documents/source/funtional/detect");

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals("/Users/ekerwin/Documents/source/funtional/detect", resolved.toString(), "Tilde's should be resolved on Unix operating systems.");
    }

    @Test
    public void testResolvingTildeInWindows() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.WINDOWS, true);
        final String filePath = "~/Documents/source/funtional/detect";
        final Path resolved = resolver.resolvePath(filePath);

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals(filePath, resolved.toString(), "Tilde resolution should not occur on Windows.");
    }

    @Test
    public void testResolvingTildeInTheMiddleOfAPath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX, true);
        final String filePath = "/Documents/~source/~/funtional/detect";
        final Path resolved = resolver.resolvePath(filePath);

        Assertions.assertNotNull(resolved, "Resolved path should not be null.");
        Assertions.assertEquals(filePath, resolved.toString(), "Tilde's in the middle of the path should not be resolved.");
    }

    @Test
    public void testBlankPath() {
        final TildeInPathResolver resolver = new TildeInPathResolver("/Users/ekerwin", OperatingSystemType.LINUX, true);
        final List<String> filePaths = new ArrayList<>();
        filePaths.add("");
        filePaths.add(" ");
        filePaths.add("     ");

        for (final String filePath : filePaths) {
            final Path resolved = resolver.resolvePath(filePath);
            Assertions.assertNull(resolved, "A blank resolved path should be null.");
        }
    }
}
