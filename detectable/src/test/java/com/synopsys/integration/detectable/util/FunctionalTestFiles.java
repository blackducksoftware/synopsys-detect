package com.synopsys.integration.detectable.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.synopsys.integration.util.ResourceUtil;

public class FunctionalTestFiles {
    private static String resourcePrefix = "/detectables/functional";

    public static String asString(String relativeResourcePath) {
        final String data;
        try {
            data = ResourceUtil.getResourceAsString(FunctionalTestFiles.class, resourcePrefix + relativeResourcePath, StandardCharsets.UTF_8.toString());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(data.split("\r?\n")).stream().collect(Collectors.joining(System.lineSeparator()));
    }

    public static InputStream asInputStream(final String relativeResourcePath) {
        return FunctionalTestFiles.class.getResourceAsStream(resourcePrefix + relativeResourcePath);
    }
}
