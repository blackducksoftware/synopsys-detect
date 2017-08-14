/*
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
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
package com.blackducksoftware.integration.hub.detect.bomtool.cpan

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNode
import com.blackducksoftware.integration.hub.detect.nameversion.NameVersionNodeImpl

@Component
class CpanListParser {
    private final Logger logger = LoggerFactory.getLogger(CpanListParser.class)

    public Map<String, NameVersionNode> parse(String listText) {
        Map<String, NameVersionNode> moduleMap = [:]

        for (String line: listText.split(System.lineSeparator())) {
            if (!line.trim()) {
                continue
            }

            if(line.count('\t') != 1 || line.trim().contains(' ')) {
                continue
            }

            try {
                String[] module = line.split('\t')
                def nameVersionNode = new NameVersionNodeImpl()
                nameVersionNode.name = module[0].trim()
                nameVersionNode.version = module[1].trim()
                moduleMap[nameVersionNode.name] = nameVersionNode
            } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
                logger.debug("Failed to handle the following line:${line}")
            }
        }

        moduleMap
    }
}
