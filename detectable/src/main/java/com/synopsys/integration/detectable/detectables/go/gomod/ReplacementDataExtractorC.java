package com.synopsys.integration.detectable.detectables.go.gomod;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.synopsys.integration.detectable.detectable.exception.DetectableException;
import com.synopsys.integration.detectable.detectables.go.gomod.model.GoListUJsonData;
import com.synopsys.integration.detectable.detectables.go.gomod.model.ReplaceData;

public class ReplacementDataExtractorC {

    private Map<String, String> replacementData;
    private Gson gson;

    public ReplacementDataExtractorC(Map<String, String> replacementData, Gson gson) {
        this.replacementData = replacementData;
        this.gson = gson;
    }

    public void extractReplacementData(List<String> listUJsonOutput) throws DetectableException {
        // Similar to Extractor A in that we're going to delegate parsing to Gson, but we're only going to convert elements one at a time

        StringBuilder rawEntry = new StringBuilder();
        for (String line : listUJsonOutput) {
            rawEntry.append(line);
            if (line.startsWith("}")) {
                try {
                    GoListUJsonData data = gson.fromJson(rawEntry.toString(), GoListUJsonData.class);

                    ReplaceData replace = data.getReplace();
                    if (replace != null) {
                        String path = data.getPath();
                        String originalVersion = data.getVersion();
                        String replaceVersion = replace.getVersion();
                        replacementData.put(String.format("%s@%s", path, originalVersion), String.format("%s@%s", path, replaceVersion));
                    }

                    // Reset to accumulate a new entry
                    rawEntry = new StringBuilder();
                } catch (JsonSyntaxException e) {
                    throw new DetectableException(e.getMessage());
                }
            }
        }
    }
}
