/**
 * detectable
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
