/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.detectable.detectables.go.gomod;

import java.util.List;
import java.util.Map;

import com.synopsys.integration.detectable.detectable.exception.DetectableException;

public class GoModGraphTransformer {
    private ReplacementDataExtractor replacementDataExtractor;

    public GoModGraphTransformer(final ReplacementDataExtractor replacementDataExtractor) {
        this.replacementDataExtractor = replacementDataExtractor;
    }

    List<String> transformGoModGraphOutput(List<String> modGraphOutput, List<String> listUJsonOutput) throws DetectableException {
        if (!listUJsonOutput.isEmpty()) {
            Map<String, String> replacementData = replacementDataExtractor.extractReplacementData(listUJsonOutput);

            for (String line : modGraphOutput) {
                int indexOfLine = modGraphOutput.indexOf(line);
                boolean hasBeenModified = false;
                for (Map.Entry<String, String> replacement : replacementData.entrySet()) {
                    String newLine;
                    boolean shouldModify;
                    if (hasBeenModified) {
                        newLine = modGraphOutput.get(indexOfLine).replace(replacement.getKey(), replacement.getValue());
                        shouldModify = !modGraphOutput.get(indexOfLine).equals(newLine);
                    } else {
                        newLine = line.replace(replacement.getKey(), replacement.getValue());
                        shouldModify = !line.equals(newLine);
                    }
                    if (shouldModify) {
                        modGraphOutput.set(indexOfLine, newLine);
                        hasBeenModified = true;
                    }
                }
            }
        }
        return modGraphOutput;
    }

}
