/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectable.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;


public class TomlFileUtils {
    public static TomlParseResult parseFile(File tomlFile) throws IOException {
        return Toml.parse(getFileAsString(tomlFile, Charset.defaultCharset()));
    }

    private static String getFileAsString(File cargoLock, Charset encoding) throws IOException {
        List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
        return String.join(System.lineSeparator(), goLockAsList);
    }
}
