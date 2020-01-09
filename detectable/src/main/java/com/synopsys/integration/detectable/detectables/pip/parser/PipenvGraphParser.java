/**
 * detectable
 *
 * Copyright (c) 2020 Synopsys, Inc.
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
package com.synopsys.integration.detectable.detectables.pip.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;
import com.synopsys.integration.detectable.detectable.util.DetectableStringUtils;
import com.synopsys.integration.detectable.detectable.util.ParentStack;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraph;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphDependency;
import com.synopsys.integration.detectable.detectables.pip.model.PipenvGraphEntry;

public class PipenvGraphParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String VERSION_SEPARATOR = "==";
    private static final String DEPENDENCY_INDENTATION = "  ";
    private static final String DEPENDENCY_NAME_PREFIX = "- ";
    private static final String DEPENDENCY_INFO_OPENING = "[";
    private static final String DEPENDENCY_VERSION_PREFIX = "installed: ";
    private static final String DEPENDENCY_INFO_CLOSING = "]";

    public PipenvGraph parse(final List<String> pipenvGraphOutput) {
        PipenvGraphEntry entry = null;
        ParentStack<PipenvGraphDependency> parentStack = new ParentStack<>();
        List<PipenvGraphEntry> entries = new ArrayList<>();

        for (final String line : pipenvGraphOutput) {
            final int currentLevel = DetectableStringUtils.parseIndentationLevel(line, DEPENDENCY_INDENTATION);
            if (currentLevel == 0){
                entry = parseEntryFromLine(line);
                entries.add(entry);
            } else if (currentLevel == 1) {
                PipenvGraphDependency dependency = parseDependencyFromLine(line);
                parentStack.clear();
                parentStack.add(dependency);

                if (entry != null) {
                    entry.getChildren().add(dependency);
                } else {
                    logger.warn("Invalid tree indentation on line: " + line);
                }
            } else {
                PipenvGraphDependency dependency = parseDependencyFromLine(line);
                parentStack.clearDeeperThan(currentLevel - 1); //minus 1 because 0 is the entry and 1 is the first dependency.
                parentStack.getCurrent().getChildren().add(dependency);
                parentStack.add(dependency);
            }
        }
        return new PipenvGraph(entries);
    }

    private PipenvGraphEntry parseEntryFromLine(String line){
        final String[] splitLine = line.trim().split(VERSION_SEPARATOR);
        String name = splitLine[0];
        String version = splitLine[1];
        return new PipenvGraphEntry(name, version, new ArrayList<>());
    }

    private PipenvGraphDependency parseDependencyFromLine( final String line) {
        String startPiece = StringUtils.substringBefore(line, DEPENDENCY_INFO_OPENING);
        String name = StringUtils.substringAfter(startPiece, DEPENDENCY_NAME_PREFIX).trim();

        String fromBracket = StringUtils.substringAfter(line, DEPENDENCY_INFO_OPENING);
        String insideBrackets = StringUtils.substringBefore(fromBracket, DEPENDENCY_INFO_CLOSING);
        String installed = StringUtils.substringAfterLast(insideBrackets, DEPENDENCY_VERSION_PREFIX).trim();
        if (StringUtils.isBlank(installed) || StringUtils.isBlank(name)) {
            logger.warn("Failed to find dependency information for line: " + line);
        }

        return new PipenvGraphDependency(name, installed, new ArrayList<>());
    }

}
