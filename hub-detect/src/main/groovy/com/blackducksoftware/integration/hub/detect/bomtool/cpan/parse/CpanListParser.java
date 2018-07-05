/**
 * hub-detect
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan.parse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode;

public class CpanListParser {
    private final Logger logger = LoggerFactory.getLogger(CpanListParser.class);

    public Map<String, NameVersionNode> parse(final List<String> listText) {
        Map<String, NameVersionNode> moduleMap = new HashMap<>();

        for (String line : listText) {
            if (StringUtils.isBlank(line)) {
                continue;
            }

            if (StringUtils.countMatches(line, "\t") != 1 || line.trim().contains(" ")) {
                continue;
            }

            try {
                String[] module = line.trim().split("\t");
                NameVersionNode nameVersionNode = new NameVersionNode();
                nameVersionNode.setName(module[0].trim());
                nameVersionNode.setVersion(module[1].trim());
                moduleMap.put(nameVersionNode.getName(), nameVersionNode);
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                logger.debug(String.format("Failed to handle the following line:%s", line));
            }
        }

        return moduleMap;
    }

}
