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
package com.synopsys.integration.detectable.detectables.lerna;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import com.synopsys.integration.detectable.detectables.lerna.model.LernaPackage;
import com.synopsys.integration.detectable.util.MissingDependencyLogger;

public class LernaMissingDependencyLogger extends MissingDependencyLogger {
    private final List<String> lernaPackages;

    public LernaMissingDependencyLogger(List<LernaPackage> lernaPackages) {
        this.lernaPackages = lernaPackages
                                 .stream()
                                 .map(LernaPackage::getName)
                                 .map(String::toLowerCase)
                                 .collect(Collectors.toList());
    }

    @Override
    public void logWarning(final String missingDependencyName, final Logger logger, final String message) {
        if (!lernaPackages.contains(missingDependencyName.toLowerCase())) {
            super.logWarning(missingDependencyName, logger, message);
        }
    }

    @Override
    public void logError(final String missingDependencyName, final Logger logger, final String message) {
        if (!lernaPackages.contains(missingDependencyName.toLowerCase())) {
            super.logError(missingDependencyName, logger, message);
        }
    }

}
