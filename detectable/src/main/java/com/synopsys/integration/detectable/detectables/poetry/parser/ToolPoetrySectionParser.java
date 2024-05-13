package com.synopsys.integration.detectable.detectables.poetry.parser;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.synopsys.integration.detectable.detectable.util.TomlFileUtils;
import com.synopsys.integration.detectable.detectables.poetry.PoetryOptions;

public class ToolPoetrySectionParser {
    public static final String TOOL_POETRY_KEY = "tool.poetry";
    public static final String MAIN_DEPENDENCY_GROUP_KEY = "tool.poetry.dependencies";

    public static final String LEGACY_DEV_DEPENDENCY_GROUP_KEY = "tool.poetry.dev-dependencies";

    public static final String DEPENDENCY_GROUP_KEY_PREFIX = "tool.poetry.group.";
    public static final String DEPENDENCY_GROUP_KEY_SUFFIX = ".dependencies";

    public static final String DEFAULT_DEV_GROUP_NAME = "dev";

    public static final String PYTHON_COMPONENT_NAME = "python";

    public ToolPoetrySectionResult parseToolPoetrySection(@Nullable File pyprojectToml) {
        if (pyprojectToml != null) {
            try {
                TomlParseResult parseResult = TomlFileUtils.parseFile(pyprojectToml);
                if (parseResult.get(TOOL_POETRY_KEY) != null) {
                    TomlTable poetrySection = parseResult.getTable(TOOL_POETRY_KEY);
                    return ToolPoetrySectionResult.FOUND(poetrySection);
                }
            } catch (IOException e) {
                return ToolPoetrySectionResult.NOT_FOUND();
            }
        }
        return ToolPoetrySectionResult.NOT_FOUND();
    }

    public Set<String> parseRootPackages(File pyprojectToml, PoetryOptions options) {
        if (options.getExcludedGroups().isEmpty() || pyprojectToml == null) {
            return null;
        }

        Set<String> result = new HashSet<>();

        TomlParseResult parseResult;
        try {
            parseResult = TomlFileUtils.parseFile(pyprojectToml);
        } catch (IOException e) {
            throw new RuntimeException("Unable to read pyproject.toml file");
        }

        for (String key : parseResult.dottedKeySet(true)) {
            processKeyForRootPackages(parseResult, options, result, key);
        }

        result.remove(PYTHON_COMPONENT_NAME);
        return result;
    }

    private void processKeyForRootPackages(TomlParseResult parseResult, PoetryOptions options, Set<String> result, String key) {
        if (!parseResult.isTable(key)) {
            return;
        }

        TomlTable table = parseResult.getTable(key);

        if (key.equals(MAIN_DEPENDENCY_GROUP_KEY)) {
            addAllTableKeysToSet(result, table);
        } else if (key.equals(LEGACY_DEV_DEPENDENCY_GROUP_KEY)) { // in Poetry 1.0 to 1.2 this was the way of specifying dev dependencies
            if (!options.getExcludedGroups().contains(DEFAULT_DEV_GROUP_NAME)) {
                addAllTableKeysToSet(result, table);
            }
        } else if (key.startsWith(DEPENDENCY_GROUP_KEY_PREFIX) && key.endsWith(DEPENDENCY_GROUP_KEY_SUFFIX)) {
            String group = key.substring(DEPENDENCY_GROUP_KEY_PREFIX.length(), key.length() - DEPENDENCY_GROUP_KEY_SUFFIX.length());

            if (!options.getExcludedGroups().contains(group)) {
                addAllTableKeysToSet(result, table);
            }
        }
    }

    private void addAllTableKeysToSet(Set<String> set, TomlTable table) {
        set.addAll(table.dottedKeySet());
    }
}
