/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.swift;

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.swift.model.SwiftPackage;

public class SwiftCliParser {
    private final Gson gson;

    public SwiftCliParser(final Gson gson) {
        this.gson = gson;
    }

    public SwiftPackage parseOutput(final List<String> lines) {
        boolean started = false;
        final StringBuilder jsonStringBuilder = new StringBuilder();
        for (final String line : lines) {
            if (!started && line.startsWith("{")) {
                started = true;
            } else if (!started) {
                continue;
            }

            jsonStringBuilder.append(line);
            jsonStringBuilder.append(System.lineSeparator());

            if (line.startsWith("}")) {
                break;
            }
        }
        final String jsonText = jsonStringBuilder.toString();

        return gson.fromJson(jsonText, SwiftPackage.class);
    }
}
