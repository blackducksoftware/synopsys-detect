package com.synopsys.integration.detectable.detectables.swift.cli;

import java.util.List;

import com.google.gson.Gson;
import com.synopsys.integration.detectable.detectables.swift.cli.model.SwiftPackage;

public class SwiftCliParser {
    private final Gson gson;

    public SwiftCliParser(Gson gson) {
        this.gson = gson;
    }

    public SwiftPackage parseOutput(List<String> lines) {
        boolean started = false;
        StringBuilder jsonStringBuilder = new StringBuilder();
        for (String line : lines) {
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
        String jsonText = jsonStringBuilder.toString();

        return gson.fromJson(jsonText, SwiftPackage.class);
    }
}
