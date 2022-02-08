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

    // We have a good reason https://doc.bccnsoft.com/docs/rust-1.36.0-docs-html/cargo/reference/config.html
    // Cargo allows for keys that violate normal TOML spec. tomlj allows us to parse these files.
    public static TomlParseResult parseFile(File tomlFile) throws IOException {
        return Toml.parse(getFileAsString(tomlFile, Charset.defaultCharset()));
    }

    private static String getFileAsString(File cargoLock, Charset encoding) throws IOException {
        List<String> goLockAsList = Files.readAllLines(cargoLock.toPath(), encoding);
        return String.join(System.lineSeparator(), goLockAsList);
    }
}
