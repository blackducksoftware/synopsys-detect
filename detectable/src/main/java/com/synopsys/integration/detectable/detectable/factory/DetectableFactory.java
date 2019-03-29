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
package com.synopsys.integration.detectable.detectable.factory;

import com.synopsys.integration.detectable.DetectableEnvironment;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectable;
import com.synopsys.integration.detectable.detectables.bitbake.BitbakeDetectableOptions;

public class DetectableFactory {
    private final UtilityFactory utilityFactory;
    private final ExtractorFactory extractorFactory;

    public DetectableFactory(final UtilityFactory utilityFactory, final ExtractorFactory extractorFactory) {
        this.utilityFactory = utilityFactory;
        this.extractorFactory = extractorFactory;
    }

    public BitbakeDetectable bitbakeDetectable(DetectableEnvironment environment, BitbakeDetectableOptions options) {
        return new BitbakeDetectable(environment, this.utilityFactory.simpleFileFinder(), options, extractorFactory.bitbakeExtractor(), this.utilityFactory.executableResolver());
    }
}
