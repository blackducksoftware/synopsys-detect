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
package com.synopsys.integration.detectable.detectables.sbt.plugin;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

public class SbtDependencyGraphPluginLineParser implements SbtPluginLineParser {
    public Optional<SbtNode> tryParseLine(String line) {
        if (!line.startsWith("[info] ")) {
            return Optional.empty();
        }
        line = line.replace("[info] ", "");
        if (StringUtils.countMatches(line, ":") != 2) {
            return Optional.empty();
        }

        line = line.replace(" [S]", "");
        line = line.replace("+-", "  ");
        line = line.replace("|", " ");
        String[] pieces = line.trim().split(":");
        for (String piece : pieces) {
            if (!isValidSetOfCharacters(piece)) {
                return Optional.empty();
            }
        }

        if (pieces.length == 3) {
            int level = countIndentations(line, "  ");//Counting line because pieces are from trimmed
            if (level > 0) { //basically if we aren't level 0, we are offset by 1 because the opening is shifted, ie its "  +-" for the first element but we expect "+-".
                level--;
            }
            return Optional.of(new SbtNode(pieces[0], pieces[1], pieces[2], level));
        } else {
            return Optional.empty();
        }
    }

    private int countIndentations(String line, String indentation) {
        String trimmed = StringUtils.stripEnd(line, null); //null means use isWhitespace
        return StringUtils.countMatches(trimmed, indentation);
    }

    private boolean isValidSetOfCharacters(String target) {
        return target.matches("^[a-zA-Z0-9.\\-+_]+$");
    }
}
