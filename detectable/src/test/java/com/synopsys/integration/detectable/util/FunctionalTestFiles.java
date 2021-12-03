package com.synopsys.integration.detectable.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.synopsys.integration.detect.commontest.FileUtil;
import com.synopsys.integration.util.ResourceUtil;

public class FunctionalTestFiles {
    private static final String resourcePrefix = "/detectables/functional";

    public static String asString(String relativeResourcePath) {
        String data;
        try {
            data = ResourceUtil.getResourceAsString(FunctionalTestFiles.class, resourcePrefix + relativeResourcePath, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return Arrays.stream(data.split("\r?\n"))
            .collect(Collectors.joining(System.lineSeparator()));
    }

    public static List<String> asListOfStrings(String relativeResourcePath) {
        String data = asString(relativeResourcePath);

        return Arrays.asList(data.split(System.lineSeparator()));
    }

    public static InputStream asInputStream(String relativeResourcePath) {
        return FunctionalTestFiles.class.getResourceAsStream(resourcePrefix + relativeResourcePath);
    }

    public static String resolvePath(String relativeResourcePath) {
        return FunctionalTestFiles.asFile(relativeResourcePath).getAbsolutePath();
    }

    public static File asFile(String relativeResourcePath) {
        return FileUtil.asFile(FunctionalTestFiles.class, relativeResourcePath, resourcePrefix);
    }

}
