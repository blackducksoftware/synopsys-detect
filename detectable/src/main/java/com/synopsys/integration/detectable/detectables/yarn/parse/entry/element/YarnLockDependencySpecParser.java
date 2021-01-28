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
package com.synopsys.integration.detectable.detectables.yarn.parse.entry.element;

import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockDependency;
import com.synopsys.integration.detectable.detectables.yarn.parse.YarnLockLineAnalyzer;

public class YarnLockDependencySpecParser {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final YarnLockLineAnalyzer yarnLockLineAnalyzer;

    public YarnLockDependencySpecParser(YarnLockLineAnalyzer yarnLockLineAnalyzer) {
        this.yarnLockLineAnalyzer = yarnLockLineAnalyzer;
    }

    public YarnLockDependency parse(String dependencySpec) {
        StringTokenizer tokenizer = yarnLockLineAnalyzer.createDependencySpecTokenizer(dependencySpec);
        String name = yarnLockLineAnalyzer.unquote(tokenizer.nextToken());
        String version = yarnLockLineAnalyzer.unquote(tokenizer.nextToken());
        logger.info("*** parsed dep '{}' to {}:{}", dependencySpec, name, version);
        return new YarnLockDependency(name, version, false);
    }
}
