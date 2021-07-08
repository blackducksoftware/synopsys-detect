/*
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod.parse;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListAllData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListModule;

public class GoListParser {
    private final Gson gson;

    public GoListParser(Gson gson) {
        this.gson = gson;
    }

    public List<GoListAllData> parseGoListAllJsonOutput(List<String> listUJsonOutput) throws DetectableException {
        return parseGoListJsonToClass(listUJsonOutput, GoListAllData.class);
    }

    public List<GoListModule> parseGoListModuleJsonOutput(List<String> listModuleJsonOutput) throws DetectableException {
        return parseGoListJsonToClass(listModuleJsonOutput, GoListModule.class);
    }

    private <T> List<T> parseGoListJsonToClass(List<String> listJsonOutput, Class<T> classOfT) throws DetectableException {
        List<T> listEntries = new LinkedList<>();

        StringBuilder jsonEntry = new StringBuilder();
        for (String line : listJsonOutput) {
            jsonEntry.append(line);
            if (line.startsWith("}")) {
                try {
                    T data = gson.fromJson(jsonEntry.toString(), classOfT);
                    listEntries.add(data);
                    // Reset to accumulate a new entry
                    jsonEntry = new StringBuilder();
                } catch (JsonSyntaxException e) {
                    throw new DetectableException(e.getMessage());
                }
            }
        }

        return listEntries;
    }
}
