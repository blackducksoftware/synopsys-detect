/**
 * detectable
 *
 * Copyright (c) 2019 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.yarn.parse;

import org.apache.commons.lang3.StringUtils;

public class YarnLineLevelParser {
    private static final String LAST_DEPENDENCY_PREFIX = "\u2514\u2500";
    private static final String NTH_DEPENDENCY_PREFIX = "\u251C\u2500";
    private static final String INNER_LEVEL_CHARACTER = "\u2502";

    private String replaceTreeCharactersWithSpaces(final String line) {
        return replaceCharsWithSpaces(line, NTH_DEPENDENCY_PREFIX, INNER_LEVEL_CHARACTER, LAST_DEPENDENCY_PREFIX);
    }

    //Counts blocks of 3 spaces after replicating tree indicators - useful for yarn list.
    public int parseTreeLevel(final String line) {
        final String cleanedLine = replaceTreeCharactersWithSpaces(line);
        return countSpaceBlocks(cleanedLine, 3);
    }

    //Counts blocks of 2 spaces as a single indent level - useful for yarn locks.
    public int parseIndentLevel(final String line) {
        return countSpaceBlocks(line, 2);
    }

    private String replaceCharsWithSpaces(String line, final String... charsSets) {
        for (final String chars : charsSets) {
            line = line.replaceAll(chars, StringUtils.repeat(" ", chars.length()));
        }
        return line;
    }

    private int countSpaceBlocks(final String line, final int spaceBlockSize) {
        final String spaceBlock = StringUtils.repeat(" ", spaceBlockSize);
        int level = 0;
        String tmpLine = line;
        while (tmpLine.startsWith(spaceBlock)) {
            tmpLine = tmpLine.replaceFirst(spaceBlock, "");
            level++;
        }

        return level;

    }
}
