package com.synopsys.integration.detectable.detectables.poetry.parser;

import java.util.Optional;

import org.tomlj.TomlTable;

public class ToolPoetrySectionResult {
    private final boolean found;
    private final TomlTable toolPoetrySection;

    public static ToolPoetrySectionResult FOUND(TomlTable poetrySection) {
        return new ToolPoetrySectionResult(true, poetrySection);
    }

    public static ToolPoetrySectionResult NOT_FOUND() {
        return new ToolPoetrySectionResult(false, null);
    }

    private ToolPoetrySectionResult(boolean found, TomlTable toolPoetrySection) {
        this.found = found;
        this.toolPoetrySection = toolPoetrySection;
    }

    public boolean wasFound() {
        return found;
    }

    public Optional<TomlTable> getToolPoetrySection() {
        return Optional.ofNullable(toolPoetrySection);
    }
}
