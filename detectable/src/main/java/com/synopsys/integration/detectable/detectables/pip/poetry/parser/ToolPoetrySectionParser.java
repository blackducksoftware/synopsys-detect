/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.pip.poetry.parser;

import java.io.File;
import java.io.IOException;

import org.jetbrains.annotations.Nullable;
import org.tomlj.TomlParseResult;
import org.tomlj.TomlTable;

import com.synopsys.integration.detectable.detectable.util.TomlFileUtils;

public class ToolPoetrySectionParser {
    public static final String TOOL_POETRY_KEY = "tool.poetry";

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
}
