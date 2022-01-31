package com.synopsys.integration.detectable.detectable.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

public class TomlFileUtils {

    private TomlFileUtils() {
        // Hiding constructor
    }

    // TODO: We should have a good reason for not letting the library parse to model objects. This shouldn't be a utility. JM-01/2022
    public static TomlParseResult parseFile(File tomlFile) throws IOException {
        return Toml.parse(getFileAsString(tomlFile, Charset.defaultCharset()));
    }

    private static String getFileAsString(File cargoLock, Charset encoding) throws IOException {
        List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
        return String.join(System.lineSeparator(), goLockAsList);
    }
}
