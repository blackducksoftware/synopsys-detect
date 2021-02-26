/*
 * common-test
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detect.commontest;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.jupiter.api.Assertions;

public class FileUtil {
    public static File asFile(Class<?> resourceLoadingClass, final String relativeResourcePath, final String prefix) {
        final URL resource = resourceLoadingClass.getResource(prefix + relativeResourcePath);
        Assertions.assertNotNull(resource, "Could not find resource path: " + prefix + relativeResourcePath);
        final File file;
        try {
            file = new File(resource.toURI().getPath());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid file: " + resource.getPath());
        }
        Assertions.assertTrue(file.exists());

        return file;
    }

}
