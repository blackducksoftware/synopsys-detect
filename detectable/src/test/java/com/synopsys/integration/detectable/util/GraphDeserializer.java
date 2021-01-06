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
package com.synopsys.integration.detectable.util;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.bdio.graph.DependencyGraph;
import com.synopsys.integration.bdio.graph.MutableMapDependencyGraph;
import com.synopsys.integration.bdio.model.Forge;
import com.synopsys.integration.bdio.model.dependency.Dependency;
import com.synopsys.integration.bdio.model.externalid.ExternalId;
import com.synopsys.integration.bdio.model.externalid.ExternalIdFactory;

public class GraphDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(GraphDeserializer.class);

    static ExternalIdFactory externalIdFactory = new ExternalIdFactory();
    static Map<String, Forge> knownForges = Forge.getKnownForges();

    public static DependencyGraph deserialize(String text) {
        MutableMapDependencyGraph graph = new MutableMapDependencyGraph();
        //three sections
        List<String> lines = Arrays.asList(text.split("\n"));
        int currentLineIndex = 0;
        String currentLine = lines.get(0);

        while (!currentLine.startsWith("Root Dependencies")) {
            currentLineIndex++;
            currentLine = lines.get(currentLineIndex);

        }

        while (!currentLine.startsWith("Relationships")) { //In Root
            currentLineIndex++;
            currentLine = lines.get(currentLineIndex);

            if (!currentLine.startsWith("Relationships")) {
                graph.addChildToRoot(make(currentLine));
            }

        }

        Dependency parent = null;
        while (currentLineIndex + 1 < lines.size()) { //In Relationships
            currentLineIndex++;
            currentLine = lines.get(currentLineIndex);

            if (currentLine.startsWith("\t\t")) {
                graph.addChildWithParent(make(currentLine), parent);
            } else if (currentLine.startsWith("\t")) {
                parent = make(currentLine);
            }
        }

        return graph;
    }

    private static Dependency make(String line) {
        String[] pieces = line.trim().split(",");
        if (pieces.length <= 3) {
            logger.debug("helpy!");
        }
        String name = unescape(pieces[0]);
        String version = unescape(pieces[1]);
        Forge forge = knownForges.get(unescape(pieces[2]));
        ExternalId externalId = externalIdFromString(forge, Arrays.asList(pieces).stream().skip(3).collect(Collectors.toList()));
        return new Dependency(name, version, externalId);
    }

    private static String unescape(String target) {
        return target.replaceAll("%commma%", ",");
    }

    private static ExternalId externalIdFromString(Forge forge, List<String> text) {
        String[] pieces = text.stream().map(it -> unescape(it)).collect(Collectors.toList()).toArray(ArrayUtils.EMPTY_STRING_ARRAY);
        return externalIdFactory.createModuleNamesExternalId(forge, pieces);
    }
}
