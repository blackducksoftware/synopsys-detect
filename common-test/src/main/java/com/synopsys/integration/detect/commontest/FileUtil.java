package com.synopsys.integration.detect.commontest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;

public class FileUtil {
    public static File asFile(Class<?> resourceLoadingClass, String relativeResourcePath, String prefix) {
        URL resource = resourceLoadingClass.getResource(prefix + relativeResourcePath);
        Assertions.assertNotNull(resource, "Could not find resource path: " + prefix + relativeResourcePath);
        File file;
        try {
            file = new File(resource.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid file: " + resource.getPath());
        }
        Assertions.assertTrue(file.exists());

        return file;
    }

}
