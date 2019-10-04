package com.synopsys.integration.detect.battery;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;

import com.synopsys.integration.util.ResourceUtil;

public class BatteryFiles {
    private static final String resourcePrefix = "/battery";

    public static String asString(final String relativeResourcePath) {
        final String data;
        try {
            data = ResourceUtil.getResourceAsString(BatteryFiles.class, resourcePrefix + relativeResourcePath, StandardCharsets.UTF_8);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.stream(data.split("\r?\n"))
                   .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> asListOfStrings(final String relativeResourcePath) {
        final String data = asString(relativeResourcePath);

        return Arrays.asList(data.split(System.lineSeparator()));
    }

    public static InputStream asInputStream(final String relativeResourcePath) {
        return BatteryFiles.class.getResourceAsStream(resourcePrefix + relativeResourcePath);
    }

    public static String resolvePath(final String relativeResourcePath) {
        return BatteryFiles.asFile(relativeResourcePath).getAbsolutePath();
    }

    public static File asFile(final String relativeResourcePath) {
        final URL resource = BatteryFiles.class.getResource(resourcePrefix + relativeResourcePath);
        final File file = new File(resource.getFile());
        Assert.assertTrue(file.exists());

        return file;
    }
}
